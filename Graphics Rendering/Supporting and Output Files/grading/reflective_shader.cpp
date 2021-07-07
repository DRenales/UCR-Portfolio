#include "reflective_shader.h"
#include "ray.h"
#include "render_world.h"

vec3 Reflective_Shader::
Shade_Surface(const Ray& ray,const vec3& intersection_point,
    const vec3& normal,int recursion_depth) const
{
    vec3 color;
    TODO; // determine the color

    Ray reflection(intersection_point, (ray.direction - normal * 2 * dot(ray.direction, normal)).normalized());

    vec3 reflection_color = this->shader->world.Cast_Ray(reflection, ++recursion_depth);
    vec3 shader_color = this->shader->Shade_Surface(ray, intersection_point, normal, recursion_depth);

    return (this->reflectivity * reflection_color + (1 - this->reflectivity) * shader_color);
}
