--CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION destroy_chat()
RETURNS trigger
AS $tg$
    DECLARE rowCount integer;
BEGIN
    DELETE FROM CHAT_LIST WHERE chat_id = old.chat_id;
    DELETE FROM MESSAGE WHERE chat_id = old.chat_id;
    IF found THEN
        GET DIAGNOSTICS rowCount = ROW_COUNT;
        RAISE NOTICE 'DELETE % row(s)', rowCount;
    END IF;
    RETURN NULL;
END;
$tg$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION destroy_user()
RETURNS trigger
AS $tg$
    DECLARE rCount integer;
BEGIN
    DELETE FROM USER_LIST WHERE list_id = old.contact_list;
    DELETE FROM USER_LIST WHERE list_id = old.block_list;
    DELETE FROM MESSAGE WHERE sender_login = old.login;
    IF found THEN
        GET DIAGNOSTICS rCount = ROW_COUNT;
        RAISE NOTICE 'DELETE % row(s)', rCount;
    END IF;
    RETURN NULL;
END;
$tg$ LANGUAGE plpgsql;

DROP TRiGGER destroy_chat_trigger ON CHAT;
CREATE TRIGGER destroy_chat_trigger
BEFORE DELETE ON CHAT
FOR EACH ROW
EXECUTE PROCEDURE destroy_chat();

DROP TRiGGER destroy_user_trigger ON USR;
CREATE TRIGGER destroy_user_trigger
BEFORE DELETE ON USR
FOR EACH ROW
EXECUTE PROCEDURE destroy_user();



