package io.springboot.twitter.common;


public interface Messages {

	
    Message<Void> SUCCESS = Message.success(null);
    
    Message<Void> UNAUTHORIZED = Message.fail(Message.Code.UNAUTHORIZED);

    Message<Void> FORBIDDEN = Message.fail(Message.Code.FORBIDDEN);
}
