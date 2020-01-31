package com.starter.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * mysql 代码生成器演示例子
 * </p>
 *
 * @author jobob
 * @since 2018-09-12
 */
public class MyBatisPlusGenerator {
    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator generator = new AutoGenerator();

        // 全局配置
        GlobalConfig config = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        config.setOutputDir(projectPath + "/src/main/java");
        config.setAuthor("Ding");
        config.setOpen(false);
        generator.setGlobalConfig(config);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/starter?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("dba");
        dsc.setPassword("dba");
        generator.setDataSource(dsc);

        // 包配置
        PackageConfig pConfig = new PackageConfig();
        pConfig.setParent("com.starter");
        generator.setPackageInfo(pConfig);

        // 自定义配置
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath + "/src/main/resources/mapper/"
                        + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
            }
        };
        cfg.setFileOutConfigList(focList);
        generator.setCfg(cfg);
        generator.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setInclude("log", "user", "auth");
        strategy.setSuperEntityColumns("id");
        strategy.setTablePrefix(pConfig.getModuleName() + "_");
        strategy.setEntityLombokModel(false);
        strategy.setControllerMappingHyphenStyle(true);
        generator.setStrategy(strategy);

        // 选择freemarker引擎需要指定如下，注意pom依赖必须有！
        generator.setTemplateEngine(new FreemarkerTemplateEngine());
        generator.execute();
    }
}
