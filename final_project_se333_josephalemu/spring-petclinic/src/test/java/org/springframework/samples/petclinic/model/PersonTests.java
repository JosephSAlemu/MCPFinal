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

class PersonTests {

	@Test
	void testSetGetFirstName() {
		Person person = new Person();
		person.setFirstName("Alice");
		assertThat(person.getFirstName()).isEqualTo("Alice");
	}

	@Test
	void testSetGetLastName() {
		Person person = new Person();
		person.setLastName("Smith");
		assertThat(person.getLastName()).isEqualTo("Smith");
	}

	// EP Tests for firstName

	@Test
	void testSetFirstName_Valid_firstName_typical() {
		Person person = new Person();
		person.setFirstName("hello");
		assertThat(person.getFirstName()).isEqualTo("hello");
	}

	@Test
	void testSetFirstName_Valid_firstName_single_char() {
		Person person = new Person();
		person.setFirstName("a");
		assertThat(person.getFirstName()).isEqualTo("a");
	}

	@Test
	void testSetFirstName_Valid_firstName_with_spaces() {
		Person person = new Person();
		person.setFirstName("hello world");
		assertThat(person.getFirstName()).isEqualTo("hello world");
	}

	@Test
	void testSetFirstName_Invalid_firstName_whitespace() {
		Person person = new Person();
		person.setFirstName(" ");
		assertThat(person.getFirstName()).isEqualTo(" ");
	}

	@Test
	void testSetFirstName_Invalid_firstName_not_blank() {
		Person person = new Person();
		person.setFirstName("");
		assertThat(person.getFirstName()).isEqualTo("");
	}

	// EP Tests for lastName

	@Test
	void testSetLastName_Valid_lastName_typical() {
		Person person = new Person();
		person.setLastName("hello");
		assertThat(person.getLastName()).isEqualTo("hello");
	}

	@Test
	void testSetLastName_Valid_lastName_single_char() {
		Person person = new Person();
		person.setLastName("a");
		assertThat(person.getLastName()).isEqualTo("a");
	}

	@Test
	void testSetLastName_Valid_lastName_with_spaces() {
		Person person = new Person();
		person.setLastName("hello world");
		assertThat(person.getLastName()).isEqualTo("hello world");
	}

	@Test
	void testSetLastName_Invalid_lastName_whitespace() {
		Person person = new Person();
		person.setLastName(" ");
		assertThat(person.getLastName()).isEqualTo(" ");
	}

	@Test
	void testSetLastName_Invalid_lastName_not_blank() {
		Person person = new Person();
		person.setLastName("");
		assertThat(person.getLastName()).isEqualTo("");
	}

}
