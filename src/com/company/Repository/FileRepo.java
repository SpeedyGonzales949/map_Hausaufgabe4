package com.company.Repository;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * this class implements the logic for parent Repository for each model
 * @param <T> The model Class for each Repository
 */
public abstract class FileRepo<T> implements CrudRepo<T> {
    protected List<T> repoList;
    public FileRepo(){
        this.repoList=new ArrayList<>();
    }
    /**
     * this method searches for an entity in the list
     * {@inheritDoc}
     */
    @Override
    public abstract T findOne(@NotNull UUID id);

    /**
     * retrieves all the data in the repository
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll() {
        return this.repoList;
    }

    /**
     * this method save an entity to the list
     * {@inheritDoc}
     */
    @Override
    public T save(@NotNull T entity) {
        for(T x:this.repoList){
            if(x.equals(entity)){
                return x;
            }
        }
        this.repoList.add(entity);
        return null;
    }
    /**
     * abstract method for deleting an entity
     * {@inheritDoc}
     */
    @Override
    public abstract T delete(@NotNull UUID id);

    /**
     * abstract method for updating en entity
     * {@inheritDoc}
     */
    @Override
    public abstract T update(@NotNull T entity);

    /**
     * this method saves the data from a Repository in a file
     * @throws IOException Input or Output error when using a file
     */
    public void saveToFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter= objectMapper.writer(new DefaultPrettyPrinter());
        objectWriter.writeValue(new File("src\\main\\resources\\"+getGenericName()+".json"),this.repoList);

    }

    /**
     * abstract method for reading Repository Data from a file
     * @throws IOException Input or Output error when using a file
     */
    public  abstract void readFromFile() throws IOException;

    /**
     * this method retrieves the name of the Model that is used for this repository {@code <T>}
     * @return the name of the class
     */
    protected String getGenericName()
    {
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName();
    }

    @Override
    public String toString() {
        return "FileRepo{" +
                "repoList=" + repoList +
                '}';
    }


}
