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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PetController.class)
@Import(PetTypeFormatter.class)
class PetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private PetTypeRepository types;

	@SuppressWarnings("unused")
	private Owner buildOwner() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");
		return owner;
	}

	private PetType buildCatType() {
		PetType cat = new PetType();
		cat.setId(1);
		cat.setName("cat");
		return cat;
	}

	@BeforeEach
	void mockCommonBehavior() {
		PetType cat = buildCatType();
		given(types.findPetTypes()).willReturn(List.of(cat));
		given(owners.findById(1)).willReturn(Optional.of(buildOwner()));
	}

	@Test
	void initCreationForm_returnsCreateForm() throws Exception {
		mockMvc.perform(get("/owners/1/pets/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processCreationForm_validPet_redirectsToOwner() throws Exception {
		mockMvc
			.perform(post("/owners/1/pets/new").param("name", "Fluffy")
				.param("birthDate", "2020-01-01")
				.param("type", "cat"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void processCreationForm_futureBirthDate_returnsForm() throws Exception {
		String futureDate = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(
				post("/owners/1/pets/new").param("name", "Fluffy").param("birthDate", futureDate).param("type", "cat"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processCreationForm_duplicateName_returnsForm() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet existingPet = new Pet();
		existingPet.setId(5); // not new → found by getPet(name, ignoreNew=true)
		existingPet.setName("Fluffy");
		ownerWithPet.getPets().add(existingPet);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		mockMvc
			.perform(post("/owners/1/pets/new").param("name", "Fluffy")
				.param("birthDate", "2020-01-01")
				.param("type", "cat"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void initUpdateForm_returnsCreateForm() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(buildCatType());
		ownerWithPet.getPets().add(pet);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		mockMvc.perform(get("/owners/1/pets/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateForm_validPet_redirectsToOwner() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(buildCatType());
		ownerWithPet.getPets().add(pet);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("name", "Fluffy")
				.param("birthDate", "2020-01-01")
				.param("type", "cat"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void processUpdateForm_invalidPet_returnsForm() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		ownerWithPet.getPets().add(pet);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		// blank name triggers validation error
		mockMvc.perform(
				post("/owners/1/pets/1/edit").param("name", "").param("birthDate", "2020-01-01").param("type", "cat"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateForm_duplicateName_returnsForm() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Fluffy");
		pet1.setBirthDate(LocalDate.of(2020, 1, 1));
		pet1.setType(buildCatType());
		Pet pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Buddy");
		pet2.setBirthDate(LocalDate.of(2019, 1, 1));
		pet2.setType(buildCatType());
		// pet2 must be first so getPet("Buddy") finds it before pet1 (which also gets
		// renamed to "Buddy" by form binding)
		ownerWithPet.getPets().add(pet2);
		ownerWithPet.getPets().add(pet1);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		// Edit pet1 with the same name as pet2 → duplicate
		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("name", "Buddy")
				.param("birthDate", "2020-01-01")
				.param("type", "cat"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	// EP Tests from generate_equivalence_class_tests

	@Test
	void testUpdatePetDetails_Invalid_owner_null_throws() {
		// When owner is not found, findById throws → MockMvc rethrows in Spring 6+
		given(owners.findById(99)).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/99/pets/new"))).isInstanceOf(Exception.class);
	}

	@Test
	void testFindPet_Invalid_petId_null_returnsNewPet() throws Exception {
		// GET /owners/1/pets/new → petId=null → returns new Pet
		mockMvc.perform(get("/owners/1/pets/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void findPet_ownerNotFound_throwsException() {
		// lambda$findPet$0: findOwner OK (1st call), findPet's findById empty (2nd call)
		PetType cat = buildCatType();
		Owner ownerNoPets = buildOwner();
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));
		pet.setType(cat);
		ownerWithPet.getPets().add(pet);

		given(types.findPetTypes()).willReturn(java.util.List.of(cat));
		given(owners.findById(1)).willReturn(Optional.of(ownerNoPets)).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/1/pets/1/edit"))).isInstanceOf(Exception.class);
	}

	@Test
	void processUpdateForm_futureBirthDate_returnsForm() throws Exception {
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));
		pet.setType(buildCatType());
		ownerWithPet.getPets().add(pet);
		given(owners.findById(1)).willReturn(Optional.of(ownerWithPet));

		String futureDate = java.time.LocalDate.now().plusDays(1).toString();
		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("name", "Fluffy")
				.param("birthDate", futureDate)
				.param("type", "cat"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateForm_petNotInOwner_coversElseBranch() throws Exception {
		// else branch in updatePetDetails: owner.getPet(id) returns null
		PetType cat = buildCatType();
		Owner ownerWithPet = buildOwner();
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));
		pet.setType(cat);
		ownerWithPet.getPets().add(pet);

		Owner ownerWithoutPet = buildOwner(); // no pets

		given(types.findPetTypes()).willReturn(java.util.List.of(cat));
		// first call (findOwner): ownerWithoutPet; second call (findPet): ownerWithPet
		given(owners.findById(1)).willReturn(Optional.of(ownerWithoutPet)).willReturn(Optional.of(ownerWithPet));

		mockMvc
			.perform(post("/owners/1/pets/1/edit").param("name", "Fluffy")
				.param("birthDate", "2020-01-01")
				.param("type", "cat"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

}
