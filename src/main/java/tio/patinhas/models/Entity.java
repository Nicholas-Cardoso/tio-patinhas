package tio.patinhas.models;

public abstract class Entity {
    private Long id;

    public Entity() {
    }

    public Entity(Long id) {
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id inválido");
        }
        this.id = id;
    }
}
