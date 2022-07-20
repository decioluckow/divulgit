package org.divulgit.bitbucket.repositorie;
import bitbucket.project.BitBucketRepository;
import bitbucket.project.RepositoryResponseHandler;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositorieRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        RepositoryResponseHandler handler = new RepositoryResponseHandler();
        BitBucketRepository bitBucketRepository = handler.handle200ResponseSingleResult(ResponseEntity.ok(json));

        assertEquals("{d7ea2443-314e-4fd1-a1b3-11fcdd63eb0e}", bitBucketRepository.getExternalId());
        assertEquals("teste_api_bitbucket", bitBucketRepository.getName());
        assertEquals("", bitBucketRepository.getDescription());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket", bitBucketRepository.getUrl());
    }

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        RepositoryResponseHandler handler = new RepositoryResponseHandler();

        List<BitBucketRepository> bitBucketRepositories = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));
        assertEquals(2, bitBucketRepositories.size());

        BitBucketRepository bitBucketRepository = bitBucketRepositories.get(0);
        assertEquals("{78e90912-6a72-41f7-998b-eb6e42b6d006}", bitBucketRepository.getExternalId());
        assertEquals("teste", bitBucketRepository.getName());
        assertEquals("", bitBucketRepository.getDescription());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste", bitBucketRepository.getUrl());

        BitBucketRepository secondBitBucketRepository = bitBucketRepositories.get(1);
        assertEquals("{d7ea2443-314e-4fd1-a1b3-11fcdd63eb0e}", secondBitBucketRepository.getExternalId());
        assertEquals("teste_api_bitbucket", secondBitBucketRepository.getName());
        assertEquals("", secondBitBucketRepository.getDescription());
        assertEquals("https://bitbucket.org/wesleyeduardocr7/teste_api_bitbucket", secondBitBucketRepository.getUrl());
    }
}