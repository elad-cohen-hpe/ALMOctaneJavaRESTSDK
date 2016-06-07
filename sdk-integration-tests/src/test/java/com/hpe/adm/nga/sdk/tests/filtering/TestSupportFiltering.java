package com.hpe.adm.nga.sdk.tests.filtering;

import com.hpe.adm.nga.sdk.Query;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.tests.base.TestBase;
import com.hpe.adm.nga.sdk.utils.CommonUtils;
import com.hpe.adm.nga.sdk.utils.generator.DataGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Guy Guetta on 25/04/2016.
 */
public class TestSupportFiltering extends TestBase {

    public TestSupportFiltering() {
        entityName = "releases";
    }

    @Test
    public void supportEqual() throws Exception {
        testFiltering("EQ");
    }

    @Test
    public void supportLessThan() throws Exception {
        testFiltering("LT");
    }

    @Test
    public void supportGreaterThan() throws Exception {
        testFiltering("GT");
    }

    @Test
    public void supportLessEqual() throws Exception {
        testFiltering("LE");
    }

    @Test
    public void supportGreaterEqual() throws Exception {
        testFiltering("GE");
    }

    private void testFiltering(String logicalOperation) throws Exception {
        Set<FieldModel> fields = new HashSet<>();
        Collection<EntityModel> generatedEntity = DataGenerator.generateEntityModel(nga, entityName, fields);
        Collection<EntityModel> entityModels = entityList.create().entities(generatedEntity).execute();
        EntityModel entityModel = entityModels.iterator().next();
        String entityName = CommonUtils.getValueFromEntityModel(entityModel, "name");

        Query query = getQuery(entityName, logicalOperation);

        Collection<EntityModel> getEntity = entityList.get().query(query).execute();

        Assert.assertTrue(CommonUtils.isCollectionAInCollectionB(generatedEntity, getEntity));
    }

    private Query getQuery(String entityName, String logicalOperation) {
        switch (logicalOperation) {
            case "EQ":
                return new Query().field("name").equal(entityName).build();
            case "LT":
                return new Query().field("name").less("z_" + entityName).build();
            case "GT":
                return new Query().field("name").greater("a_" + entityName).build();
            case "LE":
                return new Query().field("name").lessEqual(entityName).build();
            case "GE":
                return new Query().field("name").greaterEqual(entityName).build();
            default:
                return new Query();
        }
    }
}