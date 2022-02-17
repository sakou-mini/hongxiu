package com.donglaistd.jinli.database.entity.system;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

import static com.donglaistd.jinli.database.entity.system.GameServerConfig.ServerStatue.RUNNING;
import static com.donglaistd.jinli.database.entity.system.GameServerConfig.ServerStatue.STOP;

@Document
public class GameServerConfig {

    @Id
    private String id;
    @Field
    private ServerStatue serverStatue = RUNNING;

    public String getServerId() {
        return id;
    }

    public ServerStatue getServerStatue() {
        return serverStatue;
    }

    public void stopServer(){
        this.serverStatue = STOP;
    }

    public void resumeServer(){
        this.serverStatue = RUNNING;
    }

    public boolean isStop(){
        return this.serverStatue.equals(STOP);
    }

    private GameServerConfig() {
    }

    public GameServerConfig(String id) {
        this.id = id;
    }

    public static GameServerConfig newInstance(String id){
        return new GameServerConfig(id);
    }


    public enum ServerStatue{

        STOP(-1),

        RUNNING(1);
        int statueCode;

        ServerStatue(int statueCode) {
            this.statueCode = statueCode;
        }

        public int getStatueCode() {
            return statueCode;
        }
    }
}
