from fastmcp import FastMCP
import xml.etree.ElementTree as ET
import xmltojson
import re
import os
import json

mcp = FastMCP("Demo 🚀")

@mcp.tool
def read_file(file_path: str) -> str:
    with open(file_path, 'r') as file:
        return file.read()

# write a tool to parse jacocoxml report and return the coverage percentage
@mcp.tool
def parse_jacocoxml_report(file_path: str) -> float:
    """Parse a JaCoCo XML and return."""
    tree = ET.parse(file_path)
    root = tree.getroot()
    with open(file_path, 'r') as f:    
        my_xml = f.read()
    json_data = xmltojson.parse(my_xml)
    return json_data 


# ── Equivalence Class Test Case Generator ────────────────────────────────────

def _parse_java_methods(source: str) -> list[dict]:
    """Extract method signatures and their parameter info from Java source."""
    methods = []

    # Match method declarations (public/private/protected, return type, name, params)
    method_pattern = re.compile(
        r'(?P<javadoc>/\*\*.*?\*/\s*)?'
        r'(?P<modifiers>(?:(?:public|private|protected|static|final|abstract|synchronized)\s+)*)'
        r'(?P<return_type>[\w<>\[\],\s]+?)\s+'
        r'(?P<name>[a-z]\w*)\s*'
        r'\((?P<params>[^)]*)\)\s*'
        r'(?:throws\s+[\w,\s]+)?\s*\{',
        re.DOTALL
    )

    for m in method_pattern.finditer(source):
        name        = m.group("name")
        return_type = m.group("return_type").strip()
        raw_params  = m.group("params").strip()
        modifiers   = m.group("modifiers").strip()
        javadoc     = (m.group("javadoc") or "").strip()

        # Skip keywords mistakenly matched as method names
        if not name or return_type in ("if", "while", "for", "switch", "catch", "else", "return", "new"):
            continue

        params = _parse_params(raw_params)
        eq_classes = _infer_equivalence_classes(params, source, name)

        methods.append({
            "method_name": name,
            "return_type": return_type,
            "modifiers": modifiers,
            "parameters": params,
            "javadoc": javadoc,
            "equivalence_classes": eq_classes,
        })

    return methods


def _parse_params(raw_params: str) -> list[dict]:
    """Parse a comma-separated parameter list into structured dicts."""
    if not raw_params.strip():
        return []

    params = []
    # Split on commas not inside angle brackets (generics)
    depth = 0
    current = ""
    for ch in raw_params:
        if ch in "<([":
            depth += 1
        elif ch in ">)]":
            depth -= 1
        if ch == "," and depth == 0:
            params.append(current.strip())
            current = ""
        else:
            current += ch
    if current.strip():
        params.append(current.strip())

    result = []
    for p in params:
        parts = p.split()
        if len(parts) >= 2:
            ptype = " ".join(parts[:-1])
            pname = parts[-1]
        else:
            ptype = "Object"
            pname = parts[0] if parts else "param"
        result.append({"type": ptype, "name": pname})

    return result


# Type → (valid partitions, invalid partitions, boundary hints)
_TYPE_PROFILES = {
    "int": {
        "valid": [
            {"id": "positive", "description": "Positive integer (e.g. 1)", "sample": "1"},
            {"id": "zero",     "description": "Zero",                        "sample": "0"},
            {"id": "negative", "description": "Negative integer (e.g. -1)", "sample": "-1"},
        ],
        "invalid": [
            {"id": "max_overflow",  "description": "Integer.MAX_VALUE + 1 overflow", "sample": "Integer.MAX_VALUE"},
            {"id": "min_underflow", "description": "Integer.MIN_VALUE - 1 underflow","sample": "Integer.MIN_VALUE"},
        ],
        "boundary": ["Integer.MIN_VALUE", "-1", "0", "1", "Integer.MAX_VALUE"],
    },
    "long": {
        "valid": [
            {"id": "positive", "description": "Positive long",  "sample": "1L"},
            {"id": "zero",     "description": "Zero",           "sample": "0L"},
            {"id": "negative", "description": "Negative long",  "sample": "-1L"},
        ],
        "invalid": [
            {"id": "max_overflow",  "description": "Long.MAX_VALUE overflow",  "sample": "Long.MAX_VALUE"},
            {"id": "min_underflow", "description": "Long.MIN_VALUE underflow",  "sample": "Long.MIN_VALUE"},
        ],
        "boundary": ["Long.MIN_VALUE", "-1L", "0L", "1L", "Long.MAX_VALUE"],
    },
    "double": {
        "valid": [
            {"id": "positive",     "description": "Positive double",       "sample": "1.0"},
            {"id": "zero",         "description": "Zero",                  "sample": "0.0"},
            {"id": "negative",     "description": "Negative double",       "sample": "-1.0"},
            {"id": "fractional",   "description": "Fractional value",      "sample": "0.5"},
        ],
        "invalid": [
            {"id": "nan",           "description": "NaN",                  "sample": "Double.NaN"},
            {"id": "pos_infinity",  "description": "Positive infinity",    "sample": "Double.POSITIVE_INFINITY"},
            {"id": "neg_infinity",  "description": "Negative infinity",    "sample": "Double.NEGATIVE_INFINITY"},
        ],
        "boundary": ["Double.MIN_VALUE", "-1.0", "0.0", "1.0", "Double.MAX_VALUE"],
    },
    "float": {
        "valid": [
            {"id": "positive",   "description": "Positive float",   "sample": "1.0f"},
            {"id": "zero",       "description": "Zero",             "sample": "0.0f"},
            {"id": "negative",   "description": "Negative float",   "sample": "-1.0f"},
        ],
        "invalid": [
            {"id": "nan",          "description": "NaN",            "sample": "Float.NaN"},
            {"id": "pos_infinity", "description": "Positive infinity", "sample": "Float.POSITIVE_INFINITY"},
        ],
        "boundary": ["Float.MIN_VALUE", "0.0f", "1.0f", "Float.MAX_VALUE"],
    },
    "String": {
        "valid": [
            {"id": "typical",     "description": "Typical non-empty string",  "sample": '"hello"'},
            {"id": "single_char", "description": "Single character string",   "sample": '"a"'},
            {"id": "with_spaces", "description": "String with spaces",        "sample": '"hello world"'},
        ],
        "invalid": [
            {"id": "null",        "description": "null reference",            "sample": "null"},
            {"id": "empty",       "description": "Empty string",              "sample": '""'},
            {"id": "whitespace",  "description": "Whitespace-only string",    "sample": '" "'},
        ],
        "boundary": ['"" (empty)', '"a" (single char)', '"very long string..."'],
    },
    "boolean": {
        "valid": [
            {"id": "true",  "description": "true",  "sample": "true"},
            {"id": "false", "description": "false", "sample": "false"},
        ],
        "invalid": [],
        "boundary": ["true", "false"],
    },
    "List": {
        "valid": [
            {"id": "single_element", "description": "List with one element",      "sample": "List.of(item)"},
            {"id": "multiple",       "description": "List with multiple elements", "sample": "List.of(a, b, c)"},
        ],
        "invalid": [
            {"id": "null",  "description": "null list",  "sample": "null"},
            {"id": "empty", "description": "Empty list", "sample": "List.of()"},
        ],
        "boundary": ["empty list", "single element", "large list"],
    },
    "default": {
        "valid": [
            {"id": "valid_instance",  "description": "Valid, properly initialised object", "sample": "new Object()"},
        ],
        "invalid": [
            {"id": "null", "description": "null reference", "sample": "null"},
        ],
        "boundary": ["null", "default-constructed instance"],
    },
}


def _resolve_profile(java_type: str) -> dict:
    """Return the closest type profile for a given Java type string."""
    base = java_type.replace("[]", "").split("<")[0].strip()
    if base in _TYPE_PROFILES:
        return _TYPE_PROFILES[base]
    _BOXED_TO_PRIMITIVE = {
        "Integer": "int", "Long": "long", "Double": "double",
        "Float": "float", "Short": "int", "Byte": "int",
    }
    if base in _BOXED_TO_PRIMITIVE:
        # boxed — add null as extra invalid class
        profile = dict(_TYPE_PROFILES[_BOXED_TO_PRIMITIVE[base]])
        profile["invalid"] = profile["invalid"] + [{"id": "null", "description": "null (boxed type)", "sample": "null"}]
        return profile
    for key in _TYPE_PROFILES:
        if key in java_type:
            return _TYPE_PROFILES[key]
    return _TYPE_PROFILES["default"]


def _infer_equivalence_classes(params: list[dict], source: str, method_name: str) -> list[dict]:
    """
    For each parameter produce valid + invalid equivalence classes,
    enriched with any range constraints found via annotation / javadoc scanning.
    """
    all_classes = []

    for param in params:
        ptype   = param["type"]
        pname   = param["name"]
        profile = _resolve_profile(ptype)

        # Try to detect @Min/@Max/@Size/@NotNull/@NotEmpty annotations applied to this param
        constraints = _detect_constraints(source, method_name, pname)

        # Build valid classes
        valid_classes = []
        for vc in profile["valid"]:
            cls = {
                "parameter":    pname,
                "type":         ptype,
                "partition":    "valid",
                "class_id":     f"{pname}_{vc['id']}",
                "description":  vc["description"],
                "sample_value": vc["sample"],
            }
            if constraints:
                cls["constraints_detected"] = constraints
            valid_classes.append(cls)

        # Build invalid classes
        invalid_classes = []
        for ic in profile["invalid"]:
            cls = {
                "parameter":    pname,
                "type":         ptype,
                "partition":    "invalid",
                "class_id":     f"{pname}_{ic['id']}",
                "description":  ic["description"],
                "sample_value": ic["sample"],
            }
            invalid_classes.append(cls)

        # Add constraint-driven classes (e.g. below @Min, above @Max)
        for constraint in constraints:
            if constraint["type"] == "min":
                invalid_classes.append({
                    "parameter":    pname,
                    "type":         ptype,
                    "partition":    "invalid",
                    "class_id":     f"{pname}_below_min",
                    "description":  f"Value below @Min({constraint['value']}) constraint",
                    "sample_value": str(int(constraint["value"]) - 1),
                })
                valid_classes.append({
                    "parameter":    pname,
                    "type":         ptype,
                    "partition":    "valid",
                    "class_id":     f"{pname}_at_min",
                    "description":  f"Value at @Min boundary ({constraint['value']})",
                    "sample_value": str(constraint["value"]),
                })
            elif constraint["type"] == "max":
                invalid_classes.append({
                    "parameter":    pname,
                    "type":         ptype,
                    "partition":    "invalid",
                    "class_id":     f"{pname}_above_max",
                    "description":  f"Value above @Max({constraint['value']}) constraint",
                    "sample_value": str(int(constraint["value"]) + 1),
                })
                valid_classes.append({
                    "parameter":    pname,
                    "type":         ptype,
                    "partition":    "valid",
                    "class_id":     f"{pname}_at_max",
                    "description":  f"Value at @Max boundary ({constraint['value']})",
                    "sample_value": str(constraint["value"]),
                })
            elif constraint["type"] in ("not_null", "not_empty", "not_blank"):
                # Remove the null/empty invalid class if it was already added from profile
                invalid_classes = [
                    c for c in invalid_classes
                    if not (c["parameter"] == pname and c["class_id"] in (f"{pname}_null", f"{pname}_empty"))
                ]
                invalid_classes.append({
                    "parameter":    pname,
                    "type":         ptype,
                    "partition":    "invalid",
                    "class_id":     f"{pname}_{constraint['type']}",
                    "description":  f"Violates @{constraint['type'].replace('_', '').title()} constraint",
                    "sample_value": "null" if constraint["type"] == "not_null" else '""',
                })

        # Boundary values row (informational)
        boundary_entry = {
            "parameter":      pname,
            "type":           ptype,
            "partition":      "boundary",
            "class_id":       f"{pname}_boundary_values",
            "description":    "Boundary / edge values to consider",
            "sample_value":   ", ".join(profile.get("boundary", [])),
        }

        all_classes.extend(valid_classes)
        all_classes.extend(invalid_classes)
        all_classes.append(boundary_entry)

    return all_classes


def _detect_constraints(source: str, method_name: str, param_name: str) -> list[dict]:
    """Scan source for Bean Validation annotations and javadoc hints near the method."""
    constraints = []

    # Look for @Min, @Max, @Size, @NotNull, @NotEmpty, @NotBlank
    patterns = [
        (r"@Min\s*\(\s*(?:value\s*=\s*)?(\d+)\s*\)", "min"),
        (r"@Max\s*\(\s*(?:value\s*=\s*)?(\d+)\s*\)", "max"),
        (r"@Size\s*\(\s*min\s*=\s*(\d+)", "size_min"),
        (r"@Size\s*\(\s*max\s*=\s*(\d+)", "size_max"),
        (r"(@NotNull)",  "not_null"),
        (r"(@NotEmpty)", "not_empty"),
        (r"(@NotBlank)", "not_blank"),
    ]

    # Narrow search to a window around the method
    method_idx = source.find(method_name + "(")
    if method_idx == -1:
        return constraints

    window = source[max(0, method_idx - 500): method_idx + 500]

    for pattern, ctype in patterns:
        for m in re.finditer(pattern, window):
            val = m.group(1) if m.lastindex and m.lastindex >= 1 else None
            entry = {"type": ctype}
            if val and val.isdigit():
                entry["value"] = int(val)
            constraints.append(entry)

    return constraints


def _build_test_cases(class_name: str, methods: list[dict]) -> list[dict]:
    """
    For each method, create one test case entry per equivalence class
    (one representative per partition).
    """
    test_cases = []

    for method in methods:
        method_name = method["method_name"]
        eq_classes  = method["equivalence_classes"]

        # Group by (partition, one-per-parameter combination strategy):
        # Simple strategy: one test per class_id
        for ec in eq_classes:
            if ec["partition"] == "boundary":
                # Boundary is informational — emit as a reminder, not a separate test
                continue

            test_name = _to_test_name(method_name, ec["class_id"], ec["partition"])
            test_cases.append({
                "test_method_name":  test_name,
                "target_method":     method_name,
                "class_under_test":  class_name,
                "partition":         ec["partition"],
                "class_id":          ec["class_id"],
                "parameter":         ec["parameter"],
                "description":       ec["description"],
                "sample_value":      ec["sample_value"],
                "expected_behavior": (
                    "Method executes successfully and returns expected result"
                    if ec["partition"] == "valid"
                    else "Method throws an appropriate exception or returns an error indicator"
                ),
            })

    return test_cases


def _to_test_name(method_name: str, class_id: str, partition: str) -> str:
    """Convert method + class_id to a readable JUnit test method name."""
    suffix = class_id.replace("-", "_")
    prefix = "test" + method_name[0].upper() + method_name[1:]
    qualifier = "Valid" if partition == "valid" else "Invalid"
    return f"{prefix}_{qualifier}_{suffix}"


@mcp.tool()
def generate_equivalence_class_tests(file_path: str) -> str:
    """Analyse a Java source file and generate equivalence class test case descriptors.

    For every non-trivial method found in the file, the tool:
      1. Identifies each input parameter's type.
      2. Partitions the input domain into valid and invalid equivalence classes.
      3. Detects Bean Validation constraints (@Min, @Max, @NotNull, etc.) to
         produce additional constraint-boundary classes.
      4. Emits one test-case descriptor per equivalence class.

    Returns a JSON string with the schema:
    {
      "source_file":   "<path>",
      "class_name":    "<Java class name>",
      "methods_found": <int>,
      "test_cases":    [
        {
          "test_method_name":  "<suggested JUnit method name>",
          "target_method":     "<method under test>",
          "class_under_test":  "<class name>",
          "partition":         "valid" | "invalid",
          "class_id":          "<unique class identifier>",
          "parameter":         "<parameter name>",
          "description":       "<human-readable description of the class>",
          "sample_value":      "<representative value / literal>",
          "expected_behavior": "<what the test should assert>"
        },
        ...
      ],
      "boundary_hints": [
        {
          "method":     "<method name>",
          "parameter":  "<parameter name>",
          "boundaries": "<comma-separated boundary values>"
        },
        ...
      ]
    }

    Args:
        file_path: Absolute or relative path to a .java source file.
    """
    if not os.path.isfile(file_path):
        return json.dumps({"error": f"File not found: {file_path}"}, indent=2)

    with open(file_path, "r") as f:
        source = f.read()

    # Extract class name
    class_match = re.search(r'\bclass\s+(\w+)', source)
    class_name  = class_match.group(1) if class_match else os.path.basename(file_path).replace(".java", "")

    methods      = _parse_java_methods(source)
    test_cases   = _build_test_cases(class_name, methods)

    # Collect boundary hints separately
    boundary_hints = []
    for method in methods:
        for ec in method["equivalence_classes"]:
            if ec["partition"] == "boundary":
                boundary_hints.append({
                    "method":     method["method_name"],
                    "parameter":  ec["parameter"],
                    "boundaries": ec["sample_value"],
                })

    result = {
        "source_file":   file_path,
        "class_name":    class_name,
        "methods_found": len(methods),
        "test_cases":    test_cases,
        "boundary_hints": boundary_hints,
    }

    return json.dumps(result, indent=2)


if __name__ == "__main__":
    mcp.run(transport="sse")