package dev.aperso.entice;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class EmptyClassLoader extends ClassLoader {
	public EmptyClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("net.minecraft.client")) {
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			classWriter.visit(
				Opcodes.V1_8,
				Opcodes.ACC_PUBLIC,
				name.replace(".", "/"),
				null,
				"java/lang/Object",
				null
			);
			classWriter.visitEnd();
			byte[] clazz = classWriter.toByteArray();
			return super.defineClass(name, clazz, 0, clazz.length);
		} else {
			throw new ClassNotFoundException();
		}
	}
}
