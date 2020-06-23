package io.springboot.twitter.exception;


import io.springboot.twitter.common.Message;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3293785536478590576L;

	private Message<Void> message;
	
	private Throwable throwable;

    public ServiceException(Message<Void> message){
        super(message.getMessage());
        this.message = message;
    }
    
    public ServiceException(Message<Void> message, Throwable throwable){
        super(message.getMessage());
        this.message = message;
        this.throwable = throwable;
    }

    public Message<Void> message(){
        return this.message;
    }
    
    public Throwable throwable() {
    	return throwable;
    }
}
