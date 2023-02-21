package com.hhwyz;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class FileDAO {
    int check() {
        int i;
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            i = mapper.check();
            session.commit();
        }
        return i;
    }

    int batchInsertFile(List<FileDO> fileList) {
        int i;
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            i = mapper.batchInsertFile(fileList);
            session.commit();
        }
        return i;
    }

    void deleteAll() {
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            mapper.deleteAll();
            session.commit();
        }
    }

    List<FileDO> select(String[] leftPattern, String[] midPattern, String[] rightPattern) {
        List<FileDO> result = null;
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            result = mapper.select(leftPattern, midPattern, rightPattern);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void init() {
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            mapper.init();
            session.commit();
        }
    }

    public int count() {
        int r;
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            r = mapper.count();
            session.commit();
        }
        return r;
    }

    public String selectTime() {
        String r;
        try (SqlSession session = MyBatisConfig.sqlSessionFactory.openSession()) {
            FileMapper mapper = session.getMapper(FileMapper.class);
            r = mapper.selectTime();
            session.commit();
        }
        return r;
    }
}
