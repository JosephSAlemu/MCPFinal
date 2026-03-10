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
package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NamedEntityTests {

	@Test
	void testGetName_setName_typicalString() {
		NamedEntity entity = new NamedEntity();
		entity.setName("Feline");
		assertThat(entity.getName()).isEqualTo("Feline");
	}

	@Test
	void testToString_withNonNullName_returnsName() {
		NamedEntity entity = new NamedEntity();
		entity.setName("Cat");
		assertThat(entity.toString()).isEqualTo("Cat");
	}

	@Test
	void testToString_withNullName_returnsNullPlaceholder() {
		NamedEntity entity = new NamedEntity();
		assertThat(entity.toString()).isEqualTo("<null>");
	}

	// EP Tests

	@Test
	void testSetName_Valid_name_typical() {
		NamedEntity entity = new NamedEntity();
		entity.setName("hello");
		assertThat(entity.getName()).isEqualTo("hello");
	}

	@Test
	void testSetName_Valid_name_single_char() {
		NamedEntity entity = new NamedEntity();
		entity.setName("a");
		assertThat(entity.getName()).isEqualTo("a");
	}

	@Test
	void testSetName_Valid_name_with_spaces() {
		NamedEntity entity = new NamedEntity();
		entity.setName("hello world");
		assertThat(entity.getName()).isEqualTo("hello world");
	}

	@Test
	void testSetName_Invalid_name_whitespace() {
		NamedEntity entity = new NamedEntity();
		entity.setName(" ");
		assertThat(entity.getName()).isEqualTo(" ");
	}

	@Test
	void testSetName_Invalid_name_not_blank() {
		NamedEntity entity = new NamedEntity();
		entity.setName("");
		assertThat(entity.getName()).isEqualTo("");
	}

}
