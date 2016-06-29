package lombok.eclipse.handlers;

import java.util.ArrayList;

import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.mangosdk.spi.ProviderFor;

import lombok.Obj;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;

@ProviderFor(EclipseAnnotationHandler.class)
public class HandleObj extends EclipseAnnotationHandler<Obj> {
	
	@Override public void handle(AnnotationValues<Obj> annotation, Annotation ast, EclipseNode annotationNode) {
		
		new EclipseObjHandler(ast, annotationNode, new ArrayList<ReferenceBinding>());
	
	}
	
}


