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

import org.junit.jupiter.api.Test;

class OwnerTests {

	private Owner buildOwner() {
		Owner owner = new Owner();
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");
		return owner;
	}

	@Test
	void setGetAddress() {
		Owner owner = new Owner();
		owner.setAddress("123 Main St");
		assertThat(owner.getAddress()).isEqualTo("123 Main St");
	}

	@Test
	void setGetCity() {
		Owner owner = new Owner();
		owner.setCity("Boston");
		assertThat(owner.getCity()).isEqualTo("Boston");
	}

	@Test
	void setGetTelephone() {
		Owner owner = new Owner();
		owner.setTelephone("1234567890");
		assertThat(owner.getTelephone()).isEqualTo("1234567890");
	}

	@Test
	void getPets_returnsEmptyListInitially() {
		Owner owner = new Owner();
		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void addPet_newPet_addsToList() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		// pet is new (id is null)
		owner.addPet(pet);
		assertThat(owner.getPets()).hasSize(1).contains(pet);
	}

	@Test
	void addPet_existingPet_doesNotAddToList() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setId(1);
		// pet is not new (has id)
		owner.addPet(pet);
		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void getPetByName_found() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		owner.getPets().add(pet);
		assertThat(owner.getPet("Fluffy")).isEqualTo(pet);
	}

	@Test
	void getPetByName_notFound_returnsNull() {
		Owner owner = new Owner();
		assertThat(owner.getPet("Fluffy")).isNull();
	}

	@Test
	void getPetByName_caseInsensitive() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		owner.getPets().add(pet);
		assertThat(owner.getPet("fluffy")).isEqualTo(pet);
	}

	@Test
	void getPetByName_withIgnoreNew_newPet_returnsNull() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		// pet is new (no id) and ignoreNew=true → skip it
		owner.getPets().add(pet);
		assertThat(owner.getPet("Fluffy", true)).isNull();
	}

	@Test
	void getPetByName_withIgnoreNew_existingPet_returnsIt() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setId(1);
		owner.getPets().add(pet);
		assertThat(owner.getPet("Fluffy", true)).isEqualTo(pet);
	}

	@Test
	void getPetById_found() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setId(42);
		owner.getPets().add(pet);
		assertThat(owner.getPet(42)).isEqualTo(pet);
	}

	@Test
	void getPetById_notFound_returnsNull() {
		Owner owner = new Owner();
		assertThat(owner.getPet(99)).isNull();
	}

	@Test
	void getPetById_newPet_isSkipped() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		// pet is new (id=null), so getPet(1) should return null
		owner.getPets().add(pet);
		assertThat(owner.getPet(1)).isNull();
	}

	@Test
	void addVisit_validPetAndVisit() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setId(1);
		owner.getPets().add(pet);

		Visit visit = new Visit();
		visit.setDescription("Annual checkup");
		owner.addVisit(1, visit);
		assertThat(pet.getVisits()).hasSize(1);
	}

	@Test
	void addVisit_nullPetId_throwsIllegalArgumentException() {
		Owner owner = new Owner();
		Visit visit = new Visit();
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, visit));
	}

	@Test
	void addVisit_nullVisit_throwsIllegalArgumentException() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(1);
		owner.getPets().add(pet);
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(1, null));
	}

	@Test
	void addVisit_invalidPetId_throwsIllegalArgumentException() {
		Owner owner = new Owner();
		Visit visit = new Visit();
		visit.setDescription("Test");
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(99, visit));
	}

	@Test
	void toString_containsOwnerDetails() {
		Owner owner = buildOwner();
		owner.setId(1);
		String result = owner.toString();
		assertThat(result).contains("Alice").contains("Smith").contains("1234567890");
	}

	// EP Tests (from MCP generate_equivalence_class_tests)

	@Test
	void testGetPet_Valid_name_typical() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("hello");
		owner.getPets().add(pet);
		assertThat(owner.getPet("hello")).isEqualTo(pet);
	}

	@Test
	void testGetPet_Invalid_name_null() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName(null);
		owner.getPets().add(pet);
		// getPet iterates and does compName.equalsIgnoreCase(name) - compName is null so
		// skipped
		assertThat(owner.getPet("anything")).isNull();
	}

}
