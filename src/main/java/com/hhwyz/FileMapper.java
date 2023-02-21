package com.hhwyz;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {
    int batchInsertFile(@Param("fileList") List<FileDO> fileList);

    void deleteAll();

    List<FileDO> select(@Param("leftPattern") String[] leftPattern, @Param("midPattern") String[] midPattern, @Param("rightPattern") String[] rightPattern);

    int check();

    void init();

    int count();

    String selectTime();
}
