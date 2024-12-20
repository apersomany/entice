package dev.aperso.entice.packet.indicator;

import dev.aperso.entice.packet.PacketSystem;

public enum Shape {
    CIRCLE {
        public float radius;

        @Override
        public float getX() {
            return radius;
        }

        @Override
        public float getY() {
            return 0;
        }
    },
    BOX {
        public float width;
        public float height;

        @Override
        public float getX() {
            return width;
        }

        @Override
        public float getY() {
            return height;
        }
    },
    PIE {
        public float aperture;
        public float radius;

        @Override
        public float getX() {
            return aperture;
        }

        @Override
        public float getY() {
            return radius;
        }
    };

    public abstract float getX();

    public abstract float getY();

    static {
        PacketSystem.register(Shape.class);
    }
}
