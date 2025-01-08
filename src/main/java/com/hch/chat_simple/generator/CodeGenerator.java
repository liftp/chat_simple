// package com.hch.chat_simple.generator;

// import org.apache.ibatis.type.JdbcType;

// import com.baomidou.mybatisplus.annotation.FieldFill;
// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.generator.FastAutoGenerator;
// import com.baomidou.mybatisplus.generator.config.OutputFile;
// import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
// import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
// import com.baomidou.mybatisplus.generator.fill.Column;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.Collections;

// public class CodeGenerator {
//     private static final String DB_URL = "jdbc:mysql://localhost:3306/chat?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false";
//     private static final String USERNAME = "root";
//     private static final String PWD = "root";
    
//     private static final String PACKAGE_NAME = "";
//     private static final String TABLE_NAME = "friend_relationship";

//     public static void main(String[] args) {
//         String path = System.getProperty("user.dir");
//         Map<OutputFile, String> outputMap = new HashMap<>(1);
//         outputMap.put(OutputFile.xml, path + "\\src\\main\\resources\\mapper");


//         FastAutoGenerator.create(DB_URL, USERNAME, PWD)
//             .globalConfig(builder -> {
//                 builder.author("hch")
//                     .disableOpenDir()
//                     .enableSpringdoc()
//                     .outputDir(path + "\\src\\main\\java\\com\\hch\\chat_simple\\generator");
//             })
//             .dataSourceConfig(builder -> 
//                 builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
//                     if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
//                         return DbColumnType.INTEGER;
//                     }
//                     return typeRegistry.getColumnType(metaInfo);
//                 })
//             )
//             .packageConfig(builder -> 
//                 builder.parent("com.hch.chat_simple")
//                     .moduleName(PACKAGE_NAME)
//                     .service("service")
//                     .serviceImpl("service.impl")
//                     .mapper("mapper")
//                     .controller("controller")
//                     .entity("pojo")
//                     .pathInfo(outputMap)
//             )
//             .strategyConfig(builder -> 
//                 builder.addInclude(TABLE_NAME)
//                     // .addTablePrefix("chat_")
//                     .entityBuilder()
//                     .addTableFills(new Column("create_at", FieldFill.INSERT))
//                     .addTableFills(new Column("update_at", FieldFill.INSERT_UPDATE))
//                     .logicDeleteColumnName("dr")
//                     .idType(IdType.AUTO)
//                     .enableLombok()
//                     .controllerBuilder()
//                     .enableRestStyle()
//                     .build()
//             )
//             .templateEngine(new FreemarkerTemplateEngine())
//             .execute();
//     }
// }
