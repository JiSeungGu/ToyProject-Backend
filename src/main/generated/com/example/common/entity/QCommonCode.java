package com.example.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommonCode is a Querydsl query type for CommonCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommonCode extends EntityPathBase<CommonCode> {

    private static final long serialVersionUID = 1242565393L;

    public static final QCommonCode commonCode = new QCommonCode("commonCode");

    public final StringPath codegroup = createString("codegroup");

    public final StringPath codeid = createString("codeid");

    public final StringPath codename = createString("codename");

    public final StringPath desc1 = createString("desc1");

    public final StringPath desc2 = createString("desc2");

    public final StringPath desc3 = createString("desc3");

    public final StringPath showyn = createString("showyn");

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public QCommonCode(String variable) {
        super(CommonCode.class, forVariable(variable));
    }

    public QCommonCode(Path<? extends CommonCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommonCode(PathMetadata metadata) {
        super(CommonCode.class, metadata);
    }

}

