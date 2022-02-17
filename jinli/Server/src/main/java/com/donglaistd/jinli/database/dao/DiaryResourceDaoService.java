package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryResourceDaoService {

    private final DiaryResourceRepository diaryResourceRepository;
    public DiaryResourceDaoService(DiaryResourceRepository diaryResourceRepository) {
        this.diaryResourceRepository = diaryResourceRepository;
    }

    public long countDiaryResource(String diaryId){
        return diaryResourceRepository.countByDiaryId(diaryId);
    }

    public List<DiaryResource> findAllResourceByDiaryId(String diaryId){
        return diaryResourceRepository.findAllByDiaryId(diaryId);
    }

    public DiaryResource saveDiaryResource(DiaryResource diaryResource){
        return diaryResourceRepository.save(diaryResource);
    }

    public void saveAllResource(List<DiaryResource> diaryResources) {
        diaryResourceRepository.saveAll(diaryResources);
    }

    public List<String> findDiaryResource(String diaryId){
        return diaryResourceRepository.findAllByDiaryId(diaryId).stream().map(DiaryResource::getResourceUrl).collect(Collectors.toList());
    }

    public void deleteByDiaryId(String diaryId){
        diaryResourceRepository.deleteByDiaryId(diaryId);
    }
}
