package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.Music;
import com.donglaistd.jinli.database.entity.MusicList;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.WebKeyConstant.*;

@Component
public class MusicUploadService extends RequestUploadService{
    private Logger logger = Logger.getLogger(MusicUploadService.class.getName());
    @Autowired
    MusicListDaoService musicListDaoService;
    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        MusicList musicList = musicListDaoService.findByUserId(user.getId());
        if(musicList == null)
            musicList = MusicList.newInstance(user.getId());

        String[] musicParamList = uploadFileInfo.getExamParam().getOrDefault("music", new String[]{});
        JSONArray jsonArray = new JSONArray(musicParamList[0]);
        if(jsonArray.length() != uploadFileInfo.getFile_path().size()) {
            logger.warning("music param error!");
            return Constant.ResultCode.PARAM_ERROR;
        }
        String title;
        String singer;
        String time;
        Music music;
        JSONObject jsonObject;
        for (int i = 0; i <jsonArray.length() ; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            title = jsonObject.getString(MUSICTITLE);
            singer = jsonObject.getString(MUSICSINGER);
            time = jsonObject.getString(MUSICTIME);
            music = Music.newInstance(title, singer, time, uploadFileInfo.getFile_path().get(i));
            if(!musicList.addMusic(music)) {
                logger.warning("music repeated!");
                return Constant.ResultCode.REPEATED_MUSIC;
            }
        }
        musicListDaoService.save(musicList);
        return Constant.ResultCode.SUCCESS;
    }

}
