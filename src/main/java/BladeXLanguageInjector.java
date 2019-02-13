import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.jetbrains.php.blade.BladeFileType;
import com.jetbrains.php.lang.PhpLanguage;
import org.jetbrains.annotations.NotNull;

public class BladeXLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (!(host instanceof XmlAttributeValue)) return;

        if (host.getTextLength() < 2) return;

        PsiElement parent = host.getParent();

        if (!(parent instanceof XmlAttribute)) return;

        if (!isBladeXAttribute((XmlAttribute) parent)) return;

        injectionPlacesRegistrar.addPlace(
            PhpLanguage.INSTANCE,
            new TextRange(1, host.getTextLength() - 1),
            "<?php ",
            ";?>"
        );
    }

    private static boolean isBladeXAttribute(@NotNull XmlAttribute attribute) {
        String attributeName = attribute.getName();

        if (!attributeName.startsWith(":")) return false;

        PsiFile file = attribute.getContainingFile();

        if (file == null) return false;

        VirtualFile virtualFile = file.getVirtualFile();

        if (virtualFile == null) return false;

        return virtualFile.getFileType() == BladeFileType.INSTANCE;
    }
}