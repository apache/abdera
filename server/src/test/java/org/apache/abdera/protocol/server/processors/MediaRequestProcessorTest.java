package org.apache.abdera.protocol.server.processors;

import static org.junit.Assert.*;

import org.apache.abdera.protocol.server.MediaCollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.easymock.EasyMock;
import org.junit.*;

public class MediaRequestProcessorTest {

        private MediaRequestProcessor processor = new MediaRequestProcessor();
        private MediaCollectionAdapter adapterMock = EasyMock.createStrictMock(MediaCollectionAdapter.class);
        private RequestContext requestMock = EasyMock.createStrictMock(RequestContext.class);
        private ResponseContext responseMock = EasyMock.createStrictMock(ResponseContext.class);
        
        @After
        public void verify() throws Exception {
                EasyMock.verify(adapterMock);
                EasyMock.verify(requestMock);
                EasyMock.verify(responseMock);
        }
        
        @Test
        public void testProcessMediaGet() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("GET");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.getMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessMediaPost() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("POST");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.postMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessMediaPut() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("PUT");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.putMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessMediaDelete() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("DELETE");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.deleteMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessMediaHead() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("HEAD");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.headMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        @Test
        public void testProcessMediaOptions() {
                EasyMock.replay(responseMock);
                
                EasyMock.expect(requestMock.getMethod()).andReturn("OPTIONS");
                EasyMock.replay(requestMock);
                
                EasyMock.expect(adapterMock.optionsMedia(requestMock)).andReturn(responseMock);
                EasyMock.replay(adapterMock);
                
                ResponseContext response = processor.processMedia(requestMock, adapterMock);
                assertNotNull(response);
        }
        
        
}
