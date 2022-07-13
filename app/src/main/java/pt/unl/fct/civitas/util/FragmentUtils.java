package pt.unl.fct.civitas.util;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

public class FragmentUtils {

    public static void refreshFragment(Fragment frag) {
        NavController navController = NavHostFragment.findNavController(frag);
        int id = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        navController.popBackStack(id, true);
        navController.navigate(id);
    }
}
