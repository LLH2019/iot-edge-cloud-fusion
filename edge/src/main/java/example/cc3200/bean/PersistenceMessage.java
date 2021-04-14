package example.cc3200.bean;

public class PersistenceMessage {
    public abstract static class Event implements CborSerializable {
        public final String msgId;

        public Event(String msgId) {
            this.msgId = msgId;
        }
    }
}
