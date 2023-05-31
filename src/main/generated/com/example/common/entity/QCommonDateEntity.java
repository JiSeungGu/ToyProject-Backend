package com.example.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommonDateEntity is a Querydsl query type for CommonDateEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QCommonDateEntity extends EntityPathBase<CommonDateEntity> {

    private static final long serialVersionUID = -1199130795L;

    public static final QCommonDateEntity commonDateEntity = new QCommonDateEntity("commonDateEntity");

    public final DateTimePath<java.time.LocalDateTime> create_dt = createDateTime("create_dt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updated_dt = createDateTime("updated_dt", java.time.LocalDateTime.class);

    public QCommonDateEntity(String variable) {
        super(CommonDateEntity.class, forVariable(variable));
    }

    public QCommonDateEntity(Path<? extends CommonDateEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommonDateEntity(PathMetadata metadata) {
        super(CommonDateEntity.class, metadata);
    }

}

