/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestMimeMultipartProvider extends BaseResourceTest
{

   private static final Logger logger = LoggerFactory.getLogger(TestMimeMultipartProvider.class);

   private static final String TEST_URI = "http://localhost:8081/mime";

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      logger.debug("Starting up...");
      dispatcher.getRegistry().addPerRequestResource(SimpleMimeMultipartResource.class);
   }

   @Test
   public void testPut() throws Exception
   {
      HttpClient client = new HttpClient();
      List<Part> partsList = new ArrayList<Part>();
      partsList.add(new StringPart("part1", "This is Value 1"));
      partsList.add(new StringPart("part2", "This is Value 2"));
      partsList.add(new FilePart("data.txt", LocateTestData.getTestData("data.txt")));
      Part[] parts = partsList.toArray(new Part[partsList.size()]);
      PutMethod method = new PutMethod(TEST_URI);
      RequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
      method.setRequestEntity(entity);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      String responseBody = method.getResponseBodyAsString();
      Assert.assertEquals(responseBody, "Count: 3");
      method.releaseConnection();
   }

   @Test
   public void testGet() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TEST_URI);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      InputStream response = method.getResponseBodyAsStream();
      BufferedInputStream in = new BufferedInputStream(response);
      String contentType = method.getResponseHeader("content-type").getValue();
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 2);
      method.releaseConnection();
   }

   //    @Test
   //    public void testPostList() throws Exception {
   //        HttpClient client = new HttpClient();
   //        List<Part> partsList = new ArrayList<Part>();
   //        partsList.add(new StringPart("part1", "This is Value 1"));
   //        partsList.add(new StringPart("part2", "This is Value 2"));
   //        Part[] parts = partsList.toArray(new Part[partsList.size()]);
   //        PostMethod method = new PostMethod(TEST_URI);
   //        RequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
   //        method.setRequestEntity(entity);
   //        int status = client.executeMethod(method);
   //        Assert.assertEquals(HttpServletResponse.SC_OK, status);
   //        String responseBody = method.getResponseBodyAsString();
   //        //Assert.assertEquals(responseBody, "Count: 2");
   //        method.releaseConnection();
   //    }
}