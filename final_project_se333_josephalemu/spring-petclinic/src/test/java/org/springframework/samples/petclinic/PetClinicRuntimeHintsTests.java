/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.vet.Vet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PetClinicRuntimeHints}.
 */
class PetClinicRuntimeHintsTests {

	@Test
	void shouldRegisterResourcePatternHints() {
		RuntimeHints hints = new RuntimeHints();
		new PetClinicRuntimeHints().registerHints(hints, getClass().getClassLoader());

		List<String> patterns = hints.resources()
			.resourcePatternHints()
			.flatMap(h -> h.getIncludes().stream())
			.map(h -> h.getPattern())
			.collect(Collectors.toList());

		assertThat(patterns).contains("db/*", "messages/*", "mysql-default-conf");
	}

	@Test
	void shouldRegisterSerializationHints() {
		RuntimeHints hints = new RuntimeHints();
		new PetClinicRuntimeHints().registerHints(hints, getClass().getClassLoader());

		assertThat(RuntimeHintsPredicates.serialization().onType(BaseEntity.class)).accepts(hints);
		assertThat(RuntimeHintsPredicates.serialization().onType(Person.class)).accepts(hints);
		assertThat(RuntimeHintsPredicates.serialization().onType(Vet.class)).accepts(hints);
	}

	// --- Equivalence Partitioning Tests ---

	/** EC: valid classLoader - valid class loader instance */
	@Test
	void testRegisterHints_Valid_classLoader_valid_instance() {
		RuntimeHints hints = new RuntimeHints();
		ClassLoader classLoader = getClass().getClassLoader();
		new PetClinicRuntimeHints().registerHints(hints, classLoader);

		List<String> patterns = hints.resources()
			.resourcePatternHints()
			.flatMap(h -> h.getIncludes().stream())
			.map(h -> h.getPattern())
			.collect(Collectors.toList());
		assertThat(patterns).isNotEmpty();
	}

	/** EC: invalid classLoader - null classLoader (API accepts null) */
	@Test
	void testRegisterHints_Invalid_classLoader_null() {
		RuntimeHints hints = new RuntimeHints();
		// registerHints accepts null classLoader - should execute without throwing
		new PetClinicRuntimeHints().registerHints(hints, null);

		assertThat(RuntimeHintsPredicates.serialization().onType(BaseEntity.class)).accepts(hints);
		assertThat(RuntimeHintsPredicates.serialization().onType(Person.class)).accepts(hints);
		assertThat(RuntimeHintsPredicates.serialization().onType(Vet.class)).accepts(hints);
	}

}
