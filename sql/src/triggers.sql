CREATE FUNCTION destroy_all() RETURNS trigger
LANGUAGE plpgsql AS
$$
DECLARE row_count int;
BEGIN
    DELETE FROM CHAT_LIST WHERE chat_id = OLD.chat_id;
    DELETE FROM MESSAGE WHERE chat_id = OLD.chat_id;
    IF found THEN
        GET DIAGNOSTICS row_count = ROW_COUNT;
        RAISE NOTICE 'DELETE % row(s)', row_count;
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER destroy_all_trigger
BEFORE DELETE ON CHAT
FOR EACH ROW
EXECUTE PROCEDURE destroy_all();