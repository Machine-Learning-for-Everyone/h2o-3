package hex;

import org.junit.Test;
import org.junit.runner.RunWith;
import water.DKV;
import water.Key;
import water.Scope;
import water.fvec.Frame;
import water.fvec.Vec;
import water.runner.CloudSize;
import water.runner.H2ORunner;

import static org.junit.Assert.*;
import static water.TestUtil.*;

@CloudSize(1)
@RunWith(H2ORunner.class)
public class FoldAssignmentTest {

    @Test
    public void toFrame() {
        try {
            Scope.enter();
            Vec v = Scope.track(ivec(0, 1));
            FoldAssignment fa = new FoldAssignment(v, new int[]{0, 1}, true);
            Frame fr = Scope.track(fa.toFrame(Key.make("fold_assignment_frame")));
            assertArrayEquals(new String[]{"fold_assignment"}, fr.names());
            assertVecEquals(v, fr.vec("fold_assignment"), 0);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void dispose_internal() {
        try {
            Key<Vec> k;
            Scope.enter();
            Vec v = ivec(0, 1);
            k = v._key;
            Scope.track(v);
            FoldAssignment fa = new FoldAssignment(v, new int[]{0, 1}, true);
            fa.dispose();
            assertNull(DKV.getGet(k));
            assertArrayEquals(new int[]{-1, -1}, fa._foldMapping);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void dispose_userGiven() {
        FoldAssignment fa = new FoldAssignment(null, new int[]{0, 1}, false);
        fa.dispose();
        assertArrayEquals(new int[]{-1, -1}, fa._foldMapping);
    }

    @Test
    public void fromUserFoldSpecification() {
        try {
            Scope.enter();
            Vec fold = Scope.track(ivec(1, 3));
            fold.setDomain(new String[]{"NoDataFold0", "fold1", "NoDataFold2", "fold3", "NoDataFold4"});
            DKV.put(fold);
            Scope.track(fold);
            
            FoldAssignment fa = FoldAssignment.fromUserFoldSpecification(2, fold);
            assertArrayEquals(new int[]{-1, 0, -1, 1}, fa._foldMapping);
            assertFalse(fa._internal);
            assertEquals(fold, fa._fold);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void nFoldWork() {
        try {
            Scope.enter();
            Vec fold = Scope.track(ivec(1, 3));
            fold.setDomain(new String[]{"NoDataFold0", "fold1", "NoDataFold2", "fold3", "NoDataFold4"});
            DKV.put(fold);
            Scope.track(fold);
            
            assertEquals(2, FoldAssignment.nFoldWork(fold));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void fromInternalFold() {
        try {
            Scope.enter();
            Vec fold = Scope.track(ivec(0, 1));
            Scope.track(fold);

            FoldAssignment fa = FoldAssignment.fromInternalFold(2, fold);
            assertArrayEquals(new int[]{0, 1}, fa._foldMapping);
            assertTrue(fa._internal);
            assertEquals(fold, fa._fold);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void inverseMapping() {
        assertArrayEquals(new int[]{0, 1, 2}, FoldAssignment.inverseMapping(new int[]{0, 1, 2}));
        assertArrayEquals(new int[]{-1, 0, -1, 1, -1, -1, -1, 2}, FoldAssignment.inverseMapping(new int[]{1, 3, 7}));
    }

    @Test
    public void foldMapping() {
        try {
            Scope.enter();
            Vec v1 = Scope.track(cvec("fold1", "fold2"));
            assertArrayEquals(new int[]{0, 1}, FoldAssignment.foldMapping(v1));
            Vec v2 = Scope.track(ivec(1, 3));
            v2.setDomain(new String[]{"NoDataFold0", "fold1", "NoDataFold2", "fold3", "NoDataFold4"});
            DKV.put(v2);
            assertArrayEquals(new int[]{1, 3}, FoldAssignment.foldMapping(v2));
        } finally {
            Scope.exit();
        }
    }

}
