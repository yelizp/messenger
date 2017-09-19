package com.yeliz.messenger.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.BeanParam;
import javax.ws.rs.client.Client;
import com.yeliz.messenger.service.MessageService;
import com.yeliz.messenger.model.Message;
import com.yeliz.messenger.resources.beans.MessageFilterBean;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
//@Produces(value= {MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {
	
	MessageService messageService = new MessageService();

	/*
	 * Rather than getting the query parameters as by the getMessages method below, you can use bean params
	/*
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> getMessages(@QueryParam("year")  int year,
			@QueryParam("start") int start,
			@QueryParam("size") int size) {		
		if(year > 0) 
			return messageService.getAllMessagesForYear(year);
		if(start >= 0 && size >= 0) 
			return messageService.getAllMessagesPaginated(start, size);
		
		return messageService.getAllMessages();
	}
	*/
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> getJsMessages(@BeanParam MessageFilterBean filterBean) {
		if(filterBean.getYear() > 0) 
			return messageService.getAllMessagesForYear(filterBean.getYear());
		if(filterBean.getStart() >= 0 && filterBean.getSize() > 0) 
			return messageService.getAllMessagesPaginated(filterBean.getStart(), filterBean.getSize());
		
		return messageService.getAllMessages();
	}
	
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<Message> getXmlMessages(@BeanParam MessageFilterBean filterBean) {
		if(filterBean.getYear() > 0) 
			return messageService.getAllMessagesForYear(filterBean.getYear());
		if(filterBean.getStart() >= 0 && filterBean.getSize() > 0) 
			return messageService.getAllMessagesPaginated(filterBean.getStart(), filterBean.getSize());
		
		return messageService.getAllMessages();
	}
	
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
		
		Message message = messageService.getMessage(id);		
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		return messageService.getMessage(id);
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(Long.toString(message.getId()))
				.build()
				.toString();
		return uri;
	}
	
	private String getUriForProfile(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(ProfileResource.class)
				.path(message.getAuthor())
				.build()
				.toString();
		return uri;
	}
	
	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(MessageResource.class, "getCommentResource")
				.path(CommentResource.class)
				.resolveTemplate("messageId", message.getId())
				.build();
		return uri.toString();
	}
	
//	@POST
//	public Message addMessage(Message message) {
//		return messageService.addMessage(message);
//	}
	
	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		Message newMessage = messageService.addMessage(message);		
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.status(Status.CREATED)
				.entity(newMessage)				
				.build();
	}
	
	@PUT
	@Path("/{messageId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
	public Message updateMessage(@PathParam("messageId") long id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}
	
	@DELETE
	@Path("/{messageId}")	
	public Message deleteMessage(@PathParam("messageId") long id) {
		return messageService.removeMessage(id);
	}
	
	/// sub-resource
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
	
}
