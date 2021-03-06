package org.sonatype.repository.conan.internal.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sonatype.goodies.testsupport.TestSupport;
import org.sonatype.nexus.common.collect.AttributesMap;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class ConanUrlIndexerTest
    extends TestSupport
{
  private static final String DOWNLOAD_URL = "jsonformoderncpp_download_url.json";

  private static final String EXPECTED_DOWNLOAD_URL = "jsonformoderncpp_download_url_converted.json";

  @Mock
  Context context;

  @Mock
  Content content;

  @Mock
  Repository repository;


  ConanUrlIndexer underTest;

  @Before
  public void setUp() {
    underTest = new ConanUrlIndexer();
  }

  @Ignore
  @Test
  public void replacesUrl() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream("jsonformoderncpp_download_url.json");
    AttributesMap attributesMap = new AttributesMap();

    //TODO: Need to map in the tokens TokenMatcher#State
    when(context.getAttributes()).thenReturn(attributesMap);
    when(content.openInputStream()).thenReturn(inputStream);
    when(repository.getUrl()).thenReturn("http://localhost/repository/conan-proxy");

    String actual = underTest.updateAbsoluteUrls(context, content, repository);

    assertAbsoluteUrlMatches(new ByteArrayInputStream(actual.getBytes()), getClass().getResourceAsStream(EXPECTED_DOWNLOAD_URL));
  }

  private void assertAbsoluteUrlMatches(final InputStream json, final InputStream expected) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();

    TypeReference<HashMap<String, URL>> typeRef = new TypeReference<HashMap<String, URL>>() {};
    Map<String, URL> actualResponse = objectMapper.readValue(json, typeRef);
    Map<String, URL> expectedResponse = objectMapper.readValue(expected, typeRef);

    actualResponse.forEach((key, value) -> expectedResponse.get(key).equals(value));
  }
}