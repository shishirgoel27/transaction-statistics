package com.n26.transaction.controller;

import com.n26.transaction.model.TransactionRequest;
import com.n26.transaction.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

@RunWith(SpringRunner.class)
@WebMvcTest
public class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionService service;

    @Test
    public void testDeleteTransactions_clearTransactions_success() throws Exception {
        Mockito.when(service.deleteTransactions()).thenReturn(Boolean.TRUE);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/transactions"))
                    .andExpect(MockMvcResultMatchers.status()
                    .is(204));
    }

    @Test
    public void testPostTransactions_addTransactions_nonParseableJson_failed() throws Exception {
        final Instant now = Instant.now();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("{\"amount\":\"Hello\",\"timestamp\":\""+ now.toString()+"\"}"))
                                                   .andExpect(MockMvcResultMatchers.status().is(422));
    }

    @Test
    public void testPostTransactions_addTransactions_invalidJson_failed() throws Exception {
        final Instant now = Instant.now();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("{\"timestamp\":\""+ now.toString()+"\"}"))
                                                   .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void testPostTransactions_addTransactions_transactionTimestampInFuture_failed() throws Exception {
        final Instant now = Instant.now().plusSeconds(120);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":\"123.45\",\"timestamp\":\""+ now.toString()+"\"}"))
                .andExpect(MockMvcResultMatchers.status().is(422));
    }

    @Test
    public void testPostTransactions_addTransactions_olderTransaction_noContent() throws Exception {
        final Instant now = Instant.now().minusSeconds(120);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":\"123.45\",\"timestamp\":\""+ now.toString()+"\"}"))
                .andExpect(MockMvcResultMatchers.status().is(204));
    }
}
