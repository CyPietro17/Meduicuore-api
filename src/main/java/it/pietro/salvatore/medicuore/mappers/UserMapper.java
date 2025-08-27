package it.pietro.salvatore.medicuore.mappers;

import it.pietro.salvatore.medicuore.entity.MyUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

  MyUser findByUsername(String username);

  void save(MyUser myUser);

  List<MyUser> findAll();
}
