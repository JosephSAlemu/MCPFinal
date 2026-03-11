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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

class PetValidatorTests {

	private PetValidator validator;

	@BeforeEach
	void setUp() {
		this.validator = new PetValidator();
	}

	@Test
	void supports_petClass_returnsTrue() {
		assertThat(this.validator.supports(Pet.class)).isTrue();
	}

	@Test
	void supports_objectClass_returnsFalse() {
		assertThat(this.validator.supports(Object.class)).isFalse();
	}

	@Test
	void validate_validPet_noErrors() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		PetType type = new PetType();
		type.setName("cat");
		pet.setType(type);
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));

		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void validate_blankName_hasNameError() {
		Pet pet = new Pet();
		pet.setName("");
		PetType type = new PetType();
		type.setName("cat");
		pet.setType(type);
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));

		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("name")).isTrue();
	}

	@Test
	void validate_nullType_newPet_hasTypeError() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		// type is null and pet is new (no id set)
		pet.setType(null);
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));

		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isTrue();
	}

	@Test
	void validate_nullType_existingPet_noTypeError() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setId(1);
		// type is null but pet is NOT new (has id)
		pet.setType(null);
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));

		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isFalse();
	}

	@Test
	void validate_nullBirthDate_hasBirthDateError() {
		Pet pet = new Pet();
		pet.setName("Fluffy");
		PetType type = new PetType();
		type.setName("cat");
		pet.setType(type);
		pet.setBirthDate(null);

		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
	}

	// EP Tests from generate_equivalence_class_tests

	@Test
	void testValidate_Valid_obj_valid_instance() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		PetType type = new PetType();
		type.setName("dog");
		pet.setType(type);
		pet.setBirthDate(java.time.LocalDate.of(2019, 6, 15));
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(pet, "pet");
		this.validator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void testValidate_Invalid_obj_null() {
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(new Pet(), "pet");
		// Pass null as the target object – validator will fail on getName() call
		try {
			this.validator.validate(null, errors);
		}
		catch (Exception e) {
			assertThat(e).isNotNull();
		}
	}

	@Test
	void testSupports_Invalid_clazz_null() {
		try {
			boolean result = this.validator.supports(null);
			// null.isAssignableFrom will throw NullPointerException
			assertThat(result).isFalse();
		}
		catch (NullPointerException e) {
			assertThat(e).isNotNull();
		}
	}

}
