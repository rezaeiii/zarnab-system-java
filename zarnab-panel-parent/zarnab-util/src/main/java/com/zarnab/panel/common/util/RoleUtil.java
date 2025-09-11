//package com.zarnab.panel.common.util;
//
//import com.quartz.common.dto.RoleDTO;
//import com.quartz.common.dto.UserDTO;
//import com.quartz.common.exception.InsufficientRoleAccessException;
//import com.quartz.common.exception.RoleNotFoundException;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.quartz.common.constants.ConfigConstants.ROLE_HEADER_KEY;
//
//public class RoleUtil {
//
//    public static <T extends Enum<T>> Set<T> matchRolesToEnum(Class<T> enumClass) {
//        return matchRolesToEnum(enumClass, getRoleNamesFromUser());
//    }
//
//    public static <T extends Enum<T>> Set<T> matchRolesToEnum(Class<T> enumClass, UserDTO user) {
//        return matchRolesToEnum(enumClass, getRoleNamesFromUser(user));
//    }
//
//    public static <T extends Enum<T>> Set<T> matchRolesToEnum(Class<T> enumClass, Collection<RoleDTO> roleDTOs) {
//        return matchRolesToEnum(enumClass, getRoleNamesFromUser(roleDTOs));
//    }
//
//    public static boolean hasRole(UserDTO user, String... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch) || user == null) return false;
//        return hasRole(getRoleNamesFromUser(user), rolesToMatch);
//    }
//
//    public static <E extends Enum<E>> boolean hasRole(UserDTO user, E roleToMatch) {
//        if (roleToMatch == null || user == null) return false;
//        return hasRole(getRoleNamesFromUser(user), roleToMatch.name());
//    }
//
//    @SafeVarargs
//    public static <E extends Enum<E>> boolean hasRole(UserDTO user, E... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch) || user == null) return false;
//        return hasRole(getRoleNamesFromUser(user), getEnumConstantsAsArray(rolesToMatch));
//    }
//
//    public static <E extends Enum<E>> boolean hasRole(UserDTO user, Class<E> enumClass, E roleToMatch) {
//        return user.getRoles().stream()
//                .map(RoleDTO::getName)
//                .anyMatch(roleName -> roleName.equals(roleToMatch.name()));
//    }
//
//    @SafeVarargs
//    public static <E extends Enum<E>> void iHaveRoleOrThrowException(E... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch)) throw new RoleNotFoundException();
//        else if (!iHaveRole(rolesToMatch))
//            throw new InsufficientRoleAccessException();
//    }
//
//    public static void iHaveRoleOrThrowException(String... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch)) throw new RoleNotFoundException();
//        else if (!iHaveRole(rolesToMatch))
//            throw new InsufficientRoleAccessException();
//    }
//
//    public static <E extends Enum<E>> void iHaveRoleOrThrowException(E roleToMatch) {
//        if (roleToMatch == null) throw new RoleNotFoundException();
//        else if (!iHaveRole(roleToMatch))
//            throw new InsufficientRoleAccessException();
//    }
//
//    public static boolean iHaveRole(String... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch)) return false;
//        return hasRole(getRoleNamesFromUser(), rolesToMatch);
//    }
//
//    public static <E extends Enum<E>> boolean iHaveRole(E roleToMatch) {
//        if (roleToMatch == null) return false;
//        return iHaveRole(roleToMatch.name());
//    }
//
//    @SafeVarargs
//    public static <E extends Enum<E>> boolean iHaveRole(E... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch)) return false;
//        return iHaveRole(getEnumConstantsAsArray(rolesToMatch));
//    }
//
//    public static boolean iHaveNoRoles() {
//        return getRoleNamesFromUser().isEmpty();
//    }
//
//    public static List<String> getNormalizedRoleNames(Set<RoleDTO> roleDTOs) {
//        return roleDTOs.stream()
//                .filter(Objects::nonNull)
//                .map(RoleDTO::getName)
//                .filter(Objects::nonNull)
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .map(String::toUpperCase)
//                .distinct()
//                .toList();
//    }
//
//    private static boolean hasRole(Collection<String> userRoleNames, String... rolesToMatch) {
//        if (isArrayNullOrEmpty(rolesToMatch) || userRoleNames.isEmpty())
//            return false;
//        try {
//            return anyMatchesInCollection(userRoleNames, rolesToMatch);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private static <T extends Enum<T>> Set<T> matchRolesToEnum(Class<T> enumClass, Set<String> userRoleNames) {
//        Set<T> matchedRoles = Arrays.stream(enumClass.getEnumConstants())
//                .filter(roleEnum -> userRoleNames.contains(roleEnum.name()))
//                .collect(Collectors.toSet());
//
//        if (matchedRoles.isEmpty()) {
//            throw new RoleNotFoundException();
//        }
//
//        return matchedRoles;
//    }
//
//    private static Set<String> getRoleNamesFromUser() {
//        String rolesHeader = RequestUtil.getCurrentRequest().getHeader(ROLE_HEADER_KEY);
//        if (rolesHeader == null || rolesHeader.isEmpty()) {
//            return Collections.emptySet();
//        }
//        return Arrays.stream(rolesHeader.split(",")).collect(Collectors.toSet());
//    }
//
//    private static Set<String> getRoleNamesFromUser(UserDTO user) {
//        return getRoleNamesFromUser(user.getRoles());
//    }
//
//    private static Set<String> getRoleNamesFromUser(Collection<RoleDTO> roleDTOs) {
//        return roleDTOs.stream().map(RoleDTO::getName).collect(Collectors.toSet());
//    }
//
//    private static <E extends Enum<E>> String [] getEnumConstantsAsArray(E[] rolesToMatch) {
//        return Arrays.stream(rolesToMatch)
//                .map(Enum::name)
//                .toArray(String[]::new);
//    }
//
//    private static <E extends Enum<E>> boolean isArrayNullOrEmpty(E[] array) {
//        return array == null || array.length == 0;
//    }
//
//    private static boolean isArrayNullOrEmpty(String[] array) {
//        return array == null || array.length == 0;
//    }
//
//    private static boolean anyMatchesInCollection (Collection<String> userRoleNames, String... rolesToMatch) {
//        return userRoleNames.stream().anyMatch(
//                roleName -> Arrays.stream(rolesToMatch).anyMatch(r -> r.equalsIgnoreCase(roleName)));
//    }
//}