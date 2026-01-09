package it.pietro.salvatore.medicuore.config.mybatis;

import it.pietro.salvatore.medicuore.mappers.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MyBatisConfig {

  public static final String TYPE_ALIASES_PACKAGE = "it.pietro.salvatore.medicuore.entity";
  public static final String CONFIG_FILE_NAME = "mybatis-config.xml";
  public static final String MAPPER_LOCATION = "classpath*:**/*-mapper.xml";

  @Value("${spring.datasource.driver-class-name}")
  private String dataSourceDriverClassName;

  @Value("${spring.datasource.url}")
  private String dataSourceUrl;

  @Value("${spring.datasource.username}")
  private String dataSourceUsername;

  @Value("${spring.datasource.password}")
  private String dataSourcePassword;

  @Bean
  @Primary
  public DataSource getDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(dataSourceDriverClassName);
    dataSource.setUrl(dataSourceUrl);
    dataSource.setUsername(dataSourceUsername);
    dataSource.setPassword(dataSourcePassword);
    return dataSource;
  }

  @Bean
  @Primary
  public PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(getDataSource());
    transactionManager.setGlobalRollbackOnParticipationFailure(true);
    return transactionManager;
  }

  @Bean
  @Primary //Only for the first SqlSessionFacotry
  public SqlSessionFactory sqlSessionFactoryBeanFieldMapper() throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(getDataSource());
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    sqlSessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATION));
    sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
    sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(CONFIG_FILE_NAME));
    return sqlSessionFactoryBean.getObject();
  }

  @Bean
  @Primary
  public MapperFactoryBean<EmployeeMapper> mapperImpiegati() throws Exception {
    MapperFactoryBean<EmployeeMapper> factoryBeanFieldMapper = new MapperFactoryBean<>(EmployeeMapper.class);
    factoryBeanFieldMapper.setSqlSessionFactory(sqlSessionFactoryBeanFieldMapper());
    return factoryBeanFieldMapper;
  }

  @Bean
  @Primary
  public MapperFactoryBean<PazienteMapper> mapperPazienti() throws Exception {
    MapperFactoryBean<PazienteMapper> factoryBeanFieldMapper = new MapperFactoryBean<>(PazienteMapper.class);
    factoryBeanFieldMapper.setSqlSessionFactory(sqlSessionFactoryBeanFieldMapper());
    return factoryBeanFieldMapper;
  }

  @Bean
  @Primary
  public MapperFactoryBean<RepartoMapper> mapperReparti() throws Exception {
    MapperFactoryBean<RepartoMapper> factoryBeanFieldMapper = new MapperFactoryBean<>(RepartoMapper.class);
    factoryBeanFieldMapper.setSqlSessionFactory(sqlSessionFactoryBeanFieldMapper());
    return factoryBeanFieldMapper;
  }

  @Bean
  @Primary
  public MapperFactoryBean<RicoveroMapper> mapperRicoveri() throws Exception {
    MapperFactoryBean<RicoveroMapper> factoryBeanFieldMapper = new MapperFactoryBean<>(RicoveroMapper.class);
    factoryBeanFieldMapper.setSqlSessionFactory(sqlSessionFactoryBeanFieldMapper());
    return factoryBeanFieldMapper;
  }

  @Bean
  @Primary
  public MapperFactoryBean<UserMapper> mapperUser() throws Exception {
    MapperFactoryBean<UserMapper> factoryBeanFieldMapper = new MapperFactoryBean<>(UserMapper.class);
    factoryBeanFieldMapper.setSqlSessionFactory(sqlSessionFactoryBeanFieldMapper());
    return factoryBeanFieldMapper;
  }
}
