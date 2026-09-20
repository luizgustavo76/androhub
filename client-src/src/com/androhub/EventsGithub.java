public class EventsGithub {
    private String id;
    private String type;
    private String actorLogin;
    private String repoName;
    private String payloadAction;
    private String payloadRef;   

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getActorLogin() { return actorLogin; }
    public void setActorLogin(String actorLogin) { this.actorLogin = actorLogin; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public String getPayloadAction() { return payloadAction; }
    public void setPayloadAction(String payloadAction) { this.payloadAction = payloadAction; }

    public String getPayloadRef() { return payloadRef; }
    public void setPayloadRef(String payloadRef) { this.payloadRef = payloadRef; }
}