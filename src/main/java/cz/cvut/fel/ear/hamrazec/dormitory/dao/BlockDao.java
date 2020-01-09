package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Block;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Objects;

@Repository
public class BlockDao extends BaseDao<Block> {
    public BlockDao() {
        super(Block.class);
    }

    public Block find(String name) {
        {
            try {
                return em.createNamedQuery("Block.findByName", Block.class).setParameter("blockname", name)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }
}
