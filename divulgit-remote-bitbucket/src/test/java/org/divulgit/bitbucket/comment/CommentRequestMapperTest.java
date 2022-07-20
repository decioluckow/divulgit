package org.divulgit.bitbucket.comment;

import bitbucket.comment.BitBucketComment;
import bitbucket.comment.BitBucketCommentResponseHandler;
import bitbucket.user.BitBucketUser;
import bitbucket.user.UserResponseHandler;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        BitBucketCommentResponseHandler handler = new BitBucketCommentResponseHandler();
        BitBucketComment bitBucketComment = handler.handle200ResponseSingleResult(ResponseEntity.ok(json));

        assertEquals(String.valueOf(11976764), bitBucketComment.getExternalId());
        assertEquals("wesleyeduardocr7", bitBucketComment.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/commits/22f9a7864f9e5e63ef274b4e547dc911728803ed#comment-11976764", bitBucketComment.getUrl());
        assertEquals("melhorar nome do atributo", bitBucketComment.getText());
    }

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        BitBucketCommentResponseHandler handler = new BitBucketCommentResponseHandler();

        List<BitBucketComment> bitBucketComments = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));
        assertEquals(2, bitBucketComments.size());

        BitBucketComment firtBitBucketComment = bitBucketComments.get(0);
        assertEquals(String.valueOf(11976764), firtBitBucketComment.getExternalId());
        assertEquals("wesleyeduardocr7", firtBitBucketComment.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/commits/22f9a7864f9e5e63ef274b4e547dc911728803ed#comment-11976764", firtBitBucketComment.getUrl());
        assertEquals("melhorar nome do atributo", firtBitBucketComment.getText());

        BitBucketComment secondBitBucketComment = bitBucketComments.get(1);
        assertEquals(String.valueOf(11976766), secondBitBucketComment.getExternalId());
        assertEquals("wesleyeduardocr7", secondBitBucketComment.getAuthor());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket/commits/22f9a7864f9e5e63ef274b4e547dc911728803ed#comment-11976766", secondBitBucketComment.getUrl());
        assertEquals("testar usar outro tipo de autenticação", secondBitBucketComment.getText());
    }
}