package com.eduquestia.backend.entity.enums;

public enum TemaVisual {
    MEDIEVAL("Medieval", "🏰", "Tema de castillos, caballeros y épica medieval"),
    ANIME("Anime", "🎌", "Tema de estilo anime japonés"),
    ESPACIAL("Espacial", "🚀", "Tema futurista espacial"),
    FANTASIA("Fantasía", "✨", "Tema mágico y fantástico"),
    CIENCIA("Ciencia", "🔬", "Tema científico y tecnológico"),
    NATURALEZA("Naturaleza", "🌿", "Tema natural y orgánico"),
    URBANO("Urbano", "🏙️", "Tema moderno y urbano"),
    OCEANO("Océano", "🌊", "Tema marino y acuático"),
    DEFAULT("Por defecto", "📚", "Tema estándar educativo");

    private final String nombre;
    private final String icono;
    private final String descripcion;

    TemaVisual(String nombre, String icono, String descripcion) {
        this.nombre = nombre;
        this.icono = icono;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIcono() {
        return icono;
    }

    public String getDescripcion() {
        return descripcion;
    }
}


