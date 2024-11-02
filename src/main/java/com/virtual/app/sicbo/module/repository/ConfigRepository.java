package com.virtual.app.sicbo.module.repository;


import com.virtual.app.sicbo.module.data.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {
    List<Config> findByUserUuid(String userUuid);
    Config findByName(String name);

    Config findByUserUuidAndName(String userUuid, String name);
}