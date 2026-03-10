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
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetTypeFormatterTests {

	@Mock
	private PetTypeRepository petTypeRepository;

	private PetTypeFormatter formatter;

	@BeforeEach
	void setUp() {
		this.formatter = new PetTypeFormatter(this.petTypeRepository);
	}

	@Test
	void print_withName_returnsName() {
		PetType petType = new PetType();
		petType.setName("cat");
		String result = this.formatter.print(petType, Locale.ENGLISH);
		assertThat(result).isEqualTo("cat");
	}

	@Test
	void print_withNullName_returnsNullPlaceholder() {
		PetType petType = new PetType();
		petType.setName(null);
		String result = this.formatter.print(petType, Locale.ENGLISH);
		assertThat(result).isEqualTo("<null>");
	}

	@Test
	void parse_typeFound_returnsPetType() throws ParseException {
		PetType cat = new PetType();
		cat.setName("cat");
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of(cat));

		PetType result = this.formatter.parse("cat", Locale.ENGLISH);
		assertThat(result.getName()).isEqualTo("cat");
	}

	@Test
	void parse_typeNotFound_throwsParseException() {
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of());

		assertThrows(ParseException.class, () -> this.formatter.parse("unknown", Locale.ENGLISH));
	}

	// EP Tests from generate_equivalence_class_tests

	@Test
	void testPrint_Valid_petType_typical_name() {
		PetType petType = new PetType();
		petType.setName("Hello");
		assertThat(this.formatter.print(petType, Locale.ENGLISH)).isEqualTo("Hello");
	}

	@Test
	void testPrint_Valid_petType_single_char_name() {
		PetType petType = new PetType();
		petType.setName("A");
		assertThat(this.formatter.print(petType, Locale.ENGLISH)).isEqualTo("A");
	}

	@Test
	void testParse_Valid_text_typical() throws ParseException {
		PetType dog = new PetType();
		dog.setName("dog");
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of(dog));

		assertThat(this.formatter.parse("dog", Locale.ENGLISH).getName()).isEqualTo("dog");
	}

	@Test
	void testParse_Valid_text_with_spaces() {
		PetType bird = new PetType();
		bird.setName("bird type");
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of(bird));

		assertThrows(ParseException.class, () -> this.formatter.parse("unknown type", Locale.ENGLISH));
	}

	@Test
	void testParse_Invalid_text_null() {
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of());
		assertThrows(ParseException.class, () -> this.formatter.parse(null, Locale.ENGLISH));
	}

	@Test
	void testParse_Invalid_text_empty() {
		given(this.petTypeRepository.findPetTypes()).willReturn(List.of());
		assertThrows(ParseException.class, () -> this.formatter.parse("", Locale.ENGLISH));
	}

}
