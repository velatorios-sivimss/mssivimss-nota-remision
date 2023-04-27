package com.imss.sivimss.notasremision.controller;

import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.imss.sivimss.notasremision.base.BaseTest;
import com.imss.sivimss.notasremision.client.MockModCatalogosClient;
import com.imss.sivimss.notasremision.security.jwt.JwtTokenProvider;
import com.imss.sivimss.notasremision.util.JsonUtil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockUser(username="10796223", password="123456",roles = "ADMIN")
public class NotasRemisionControllerTest extends BaseTest {
	 @Autowired
	 private JwtTokenProvider jwtTokenProvider;

	 @BeforeEach
	 public void setup() {
	    this.mockMvc = MockMvcBuilders
	                .webAppContextSetup(this.context)
	                .apply(springSecurity())
	                .build();
	 }
	 
	 @Test
	 @DisplayName("lista ODS")
	 @Order(1)
	 public void listaODS() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.listaODS(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/lista_ods_mock.json"), JsonUtil.readFromJson("json/response/response_lista_ods.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/v1/notasrem/listaODS")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/lista_ods_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }

	 @Test
	 @DisplayName("buscar ODS")
	 @Order(2)
	 public void buscarODS() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.buscarODS(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/buscar_ods_mock.json"), JsonUtil.readFromJson("json/response/response_buscar_ods.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/v1/notasrem/buscar")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/buscar_ods_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("generar nota")
	 @Order(3)
	 public void generarNota() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.generarNota(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/generar_nota_mock.json"), JsonUtil.readFromJson("json/response/response_generar_nota.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/v1/notasrem/genera")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/generar_nota_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle nota")
	 @Order(4)
	 public void detalleNota() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detalleNota(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_nota_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_nota.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/v1/notasrem/det-nota")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_nota_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("cancelar nota")
	 @Order(5)
	 public void cancelarNota() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detalleNota(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/cancela_nota_mock.json"), JsonUtil.readFromJson("json/response/response_cancela_nota.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/v1/notasrem/cancela")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/cancela_nota_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
}
