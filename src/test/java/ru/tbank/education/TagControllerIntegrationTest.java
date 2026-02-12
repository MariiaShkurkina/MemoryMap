package ru.tbank.education;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.education.DTO.CreateTagRequest;
import ru.tbank.education.Entity.Tag;
import ru.tbank.education.Repository.TagRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TagController Integration Tests")
class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    void cleanup() {
        tagRepository.deleteAll();
    }


    // ТЕСТЫ ДЛЯ GET /api/tags

    @Test
    @WithMockUser(roles = "ADMIN")  // ← ДОБАВЬТЕ ЭТУ СТРОКУ
    @DisplayName("GET /api/tags should return empty list when no tags exist")
    void getAll_ShouldReturnEmptyList_WhenNoTagsExist() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/tags should return all tags when tags exist")
    void getAll_ShouldReturnAllTags_WhenTagsExist() throws Exception {
        tagRepository.saveAll(List.of(
                Tag.builder().name("java").build(),
                Tag.builder().name("spring").build(),
                Tag.builder().name("testing").build()
        ));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("java"))
                .andExpect(jsonPath("$[1].name").value("spring"))
                .andExpect(jsonPath("$[2].name").value("testing"));
    }

    // ТЕСТЫ ДЛЯ POST /api/tags

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/tags should create tag when valid request")
    void create_ShouldCreateTag_WhenValidRequest() throws Exception {
        CreateTagRequest request = new CreateTagRequest();
        request.setName("newTag");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("newTag"));

        assertThat(tagRepository.findAll()).hasSize(1);
        assertThat(tagRepository.findAll().get(0).getName()).isEqualTo("newTag");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/tags should trim tag name")
    void create_ShouldTrimTagName() throws Exception {
        CreateTagRequest request = new CreateTagRequest();
        request.setName("  trimmedTag  ");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("trimmedTag"));

        assertThat(tagRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/tags should return existing tag when tag already exists (case insensitive)")
    void create_ShouldReturnExistingTag_WhenTagAlreadyExists() throws Exception {
        tagRepository.save(Tag.builder().name("java").build());

        CreateTagRequest request = new CreateTagRequest();
        request.setName("JAVA");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("java"));

        assertThat(tagRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/tags should return 400 when tag name is blank")
    void create_ShouldReturn400_WhenTagNameIsBlank() throws Exception {
        CreateTagRequest request = new CreateTagRequest();
        request.setName("   ");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        assertThat(tagRepository.findAll()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/tags should return 400 when tag name is null")
    void create_ShouldReturn400_WhenTagNameIsNull() throws Exception {
        CreateTagRequest request = new CreateTagRequest();
        request.setName(null);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        assertThat(tagRepository.findAll()).isEmpty();
    }

    // ТЕСТЫ ДЛЯ DELETE /api/tags/{tagId}

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/tags/{id} should delete tag when tag exists")
    void delete_ShouldDeleteTag_WhenTagExists() throws Exception {
        Tag savedTag = tagRepository.save(Tag.builder().name("testTag").build());
        Long tagId = savedTag.getId();

        mockMvc.perform(delete("/api/tags/{tagId}", tagId))
                .andExpect(status().isOk());

        assertThat(tagRepository.findById(tagId)).isEmpty();
    }


}