package hgw.entity;

abstract public class Entity {
    private Integer HealthPoint = 0;
    private Integer ManaPoint = 0;
    private String Name = "<generic.entity>";

    public Integer getHP() {return HealthPoint;} //gets entity health
    public void setHP(Integer HP) {HealthPoint = HP;} //sets entity health

    public Integer getMP() {return ManaPoint;} //gets entity mana
    public void setMP(Integer MP) {ManaPoint = MP;} //sets entity 
    
    public String getName() {return Name;} //gets the name
    public void setName(String _n) {Name = _n;} //sets the name
}