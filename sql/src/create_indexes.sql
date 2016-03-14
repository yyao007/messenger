CREATE INDEX index_msg 
ON MESSAGE
USING BTREE
(msg_id);

CREATE INDEX index_chat
ON Chat
USING BTREE
(chat_id);

CREATE INDEX index_usr 
ON USR
USING BTREE
(login);