package com.example.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QIssuerEntity is a Querydsl query type for IssuerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssuerEntity extends EntityPathBase<IssuerEntity> {

    private static final long serialVersionUID = -2057498922L;

    public static final QIssuerEntity issuerEntity = new QIssuerEntity("issuerEntity");

    public final StringPath issuerDid = createString("issuerDid");

    public final StringPath issuerName = createString("issuerName");

    public final StringPath issuerPk = createString("issuerPk");

    public final StringPath issuerPubk = createString("issuerPubk");

    public final NumberPath<Integer> issuerSeq = createNumber("issuerSeq", Integer.class);

    public final DateTimePath<java.sql.Timestamp> regDate = createDateTime("regDate", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> updDate = createDateTime("updDate", java.sql.Timestamp.class);

    public QIssuerEntity(String variable) {
        super(IssuerEntity.class, forVariable(variable));
    }

    public QIssuerEntity(Path<? extends IssuerEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIssuerEntity(PathMetadata metadata) {
        super(IssuerEntity.class, metadata);
    }

}

