package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.grizzlet.AsyncConnection;
import com.sun.grizzly.grizzlet.Grizzlet;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzletDispatcher extends AbstractGrizzlyDispatcher implements Grizzlet
{

   public GrizzletDispatcher(ResteasyProviderFactory providerFactory, ResourceMethodRegistry registry, String contextPath)
   {
      super(providerFactory, contextPath);
   }


   public void onRequest(AsyncConnection ac) throws IOException
   {
      GrizzlyRequest request = ac.getRequest();
      GrizzlyResponse response = ac.getResponse();


      try
      {
         ResteasyProviderFactory.pushContext(AsyncConnection.class, ac);
         invokeJaxrs(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
      response.finishResponse();
   }

   public void onPush(AsyncConnection asyncConnection) throws IOException
   {
   }

}