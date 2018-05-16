package hgw.entity;

abstract public class Entity {
    private Integer HealthPoint = 0;
    private Integer ManaPoint = 0;

    public Integer getHP() {return HealthPoint;} //gets entity health
    public void setHP(Integer HP) {HealthPoint = HP;} //sets entity health
}