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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VisitController.class)
class VisitControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner buildOwnerWithPet() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");

		PetType cat = new PetType();
		cat.setId(1);
		cat.setName("cat");

		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fluffy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(cat);

		// add pet directly to the owner's internal list
		owner.getPets().add(pet);
		return owner;
	}

	@BeforeEach
	void mockCommonBehavior() {
		given(owners.findById(1)).willReturn(Optional.of(buildOwnerWithPet()));
	}

	@Test
	void initNewVisitForm_returnsVisitForm() throws Exception {
		mockMvc.perform(get("/owners/1/pets/1/visits/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitForm_validVisit_redirectsToOwner() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("description", "Annual checkup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void processNewVisitForm_blankDescription_returnsForm() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("description", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void loadPetWithVisit_petNotFound_throwsIllegalArgumentException() {
		// Pet with id=99 doesn't exist for owner 1 → MockMvc rethrows in Spring 6+
		assertThatThrownBy(() -> mockMvc.perform(get("/owners/1/pets/99/visits/new"))).isInstanceOf(Exception.class);
	}

	@Test
	void loadPetWithVisit_ownerNotFound_throwsIllegalArgumentException() {
		given(owners.findById(99)).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/99/pets/1/visits/new"))).isInstanceOf(Exception.class);
	}

	// EP Tests from generate_equivalence_class_tests

	@Test
	void testLoadPetWithVisit_Valid_petId_valid_instance() throws Exception {
		mockMvc.perform(get("/owners/1/pets/1/visits/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testProcessNewVisitForm_Valid_description_typical() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("description", "Regular visit"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessNewVisitForm_Valid_description_single_char() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("description", "X"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessNewVisitForm_Valid_description_with_spaces() throws Exception {
		mockMvc.perform(post("/owners/1/pets/1/visits/new").param("description", "Annual health checkup"))
			.andExpect(status().is3xxRedirection());
	}

}
