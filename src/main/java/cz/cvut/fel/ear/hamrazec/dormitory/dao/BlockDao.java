package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import org.springframework.stereotype.Repository;

@Repository
public class BlockDao extends BaseDao<Block> {
        public BlockDao(){super(Block.class);}
}
