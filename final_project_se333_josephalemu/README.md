# Technical Documentation

## MCP Tool /API Documentation
> mcp jacoco-parser: 
        
        Input <str>: takes in a file_path to a jacoco.xml file

        Output <str>: outputs the json format of the jacoco.xml file

        Command: parse_jacocoxml_report(file_path: str)

        Usage Example: parse_jacocoxml_report(`final_project_se333_josephalemu/spring-petclinic/target/site/jacoco/jacoco.xml`)

> mcp tool to generate equivalence class test cases:
        
        Input <str>: takes in a Java file (not a test file)

        Output <str>: outputs the equivalent class partitioning to add to the generated Test Files.
        
        Command: generate_equivalence_class_tests(file_path: str)

        Usage Example: generate_equivalence_class_tests(`final_project_se333_josephalemu/spring-petclinic/src/main/java/org/springframework/samples/petclinic/owner/VisitController.java`)

## Installation & Configuration Guide
    1. Create a github repository on github that's empty.
    2. Copy the url and paste it into step 6 of ## Setup ## in the prompt.md
    3. Go to the `final_project_se333_josephalemu/CodeBase` directory
    4. Run `uv venv` to create a virtual environment and `source .venv\bin\active` on mac and linux to enter the virtual environment.
    5. Then run `uv pip install -r requirements.txt` to get all the required packages
    6. run `python main.py` to start the mcp server.
    7. Add your mcp server to vscode by doing `ctrl + shift + p` and adding the your server url: `http://<machine-ip>:8000/sse` in the cli.
    8. Name it anything you want.
    9. Install the github mcp server
    10. With your mcp server still running, open a chat window and click configure tools right under the prompt.
    11. Check the box for your mcp server and githubs mcp server.
    12. Ensure you are using Claude Sonnet for the VS Code chat
    13. Attach the `final_project_se333_josephalemu/.github/prompts/tester.prompt.md` file to the chat and ask it to run it as an agent.

## Troubleshooting & FAQ Section
  Q: What if it starts running slowly?

  > A: Check the context window. If it's near full capacity, shrink the context by running 
    `/compact`
