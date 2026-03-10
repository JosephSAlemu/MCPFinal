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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner buildOwner(Integer id) {
		Owner owner = new Owner();
		owner.setFirstName("Alice");
		owner.setLastName("Smith");
		owner.setAddress("123 Main St");
		owner.setCity("Springfield");
		owner.setTelephone("1234567890");
		if (id != null) {
			owner.setId(id);
		}
		return owner;
	}

	@Test
	void initCreationForm_returnsOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void initFindForm_returnsFindOwners() throws Exception {
		mockMvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(view().name("owners/findOwners"));
	}

	@Test
	void processFindForm_noResults_returnsFindOwnersWithError() throws Exception {
		given(owners.findByLastNameStartingWith(eq("NoOne"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of()));

		mockMvc.perform(get("/owners").param("lastName", "NoOne"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void processFindForm_singleResult_redirectsToOwner() throws Exception {
		Owner owner = buildOwner(1);
		given(owners.findByLastNameStartingWith(eq("Smith"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(owner)));

		mockMvc.perform(get("/owners").param("lastName", "Smith"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void processFindForm_multipleResults_returnsOwnersList() throws Exception {
		Owner o1 = buildOwner(1);
		Owner o2 = buildOwner(2);
		given(owners.findByLastNameStartingWith(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 5), 2));

		mockMvc.perform(get("/owners").param("lastName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void processFindForm_nullLastName_usesEmptyString() throws Exception {
		Owner o1 = buildOwner(1);
		given(owners.findByLastNameStartingWith(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(o1, buildOwner(2)), PageRequest.of(0, 5), 2));

		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void processCreationForm_validOwner_redirectsToOwner() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Alice")
				.param("lastName", "Smith")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void processCreationForm_invalidOwner_returnsForm() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void initUpdateOwnerForm_returnsOwnerForm() throws Exception {
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(1)));

		mockMvc.perform(get("/owners/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processUpdateOwnerForm_validOwner_redirectsToOwner() throws Exception {
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(1)));

		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "Alice")
				.param("lastName", "Smith")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1"));
	}

	@Test
	void processUpdateOwnerForm_hasErrors_returnsForm() throws Exception {
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(1)));

		mockMvc.perform(post("/owners/1/edit").param("firstName", "").param("lastName", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processUpdateOwnerForm_idMismatch_redirectsToEdit() throws Exception {
		// Owner returned from DB has null id → triggers the id-mismatch branch
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(null)));

		mockMvc
			.perform(post("/owners/1/edit").param("firstName", "Alice")
				.param("lastName", "Smith")
				.param("address", "123 Main St")
				.param("city", "Springfield")
				.param("telephone", "1234567890"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/1/edit"));
	}

	@Test
	void showOwner_returnsOwnerDetails() throws Exception {
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(1)));

		mockMvc.perform(get("/owners/1")).andExpect(status().isOk()).andExpect(view().name("owners/ownerDetails"));
	}

	// EP Tests from generate_equivalence_class_tests

	@Test
	void testProcessFindForm_Valid_page_positive() throws Exception {
		Owner o1 = buildOwner(1);
		given(owners.findByLastNameStartingWith(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(o1, buildOwner(2)), PageRequest.of(0, 5), 2));

		mockMvc.perform(get("/owners").param("lastName", "").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindForm_Valid_page_second() throws Exception {
		owner_willReturn2Pages();

		mockMvc.perform(get("/owners").param("lastName", "").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	private void owner_willReturn2Pages() {
		Owner o1 = buildOwner(1);
		Owner o2 = buildOwner(2);
		given(owners.findByLastNameStartingWith(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(o1, o2), PageRequest.of(1, 5), 12));
	}

	@Test
	void findOwner_notFound_throwsException() {
		// findOwner lambda$findOwner$0: findById returns empty → orElseThrow fires
		given(owners.findById(1)).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/1/edit"))).isInstanceOf(Exception.class);
	}

	@Test
	void showOwner_notFound_throwsException() {
		// lambda$showOwner$0: findOwner OK, showOwner's own findById returns empty
		given(owners.findById(1)).willReturn(Optional.of(buildOwner(1))).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/1"))).isInstanceOf(Exception.class);
	}

}
