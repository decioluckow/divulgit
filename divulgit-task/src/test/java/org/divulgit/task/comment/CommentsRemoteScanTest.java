package org.divulgit.task.comment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentsRemoteScanTest {

//    @Test
    public void testHashTags() {
        List<String> hashTags = CommentsRemoteScan.extractHashTag("Este é um #teste de #comentarios bem #bacana");

        assertEquals(3, hashTags.size());

    }

}