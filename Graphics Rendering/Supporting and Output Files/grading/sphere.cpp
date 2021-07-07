#include "sphere.h"
#include "ray.h"

// Determine if the ray intersects with the sphere
Hit Sphere::Intersection(const Ray& ray, int part) const
{
    //TODO;

    vec3 point = ray.endpoint - this->center;

    /*
    double a = dot(ray.direction, ray.direction);
    double b = 2.0 * dot(point, ray.direction);
    double c = dot(point, point) - (this->radius * this->radius);
    */
    double discrim = (dot(point, ray.direction) * dot(point, ray.direction) - point.magnitude_squared() + (std::pow(this->radius, 2)));

    Hit hit;
    if(discrim < 0)
    {
      hit = {NULL,0,0};
    }
    else
    {
      double p1 = -dot(point,ray.direction) + sqrt(discrim);
      double p2 = -dot(point,ray.direction) - sqrt(discrim);

      if( p1 <small_t && p2<small_t)
      {
          hit = {NULL, 0,0};
      }

      if(p1 < small_t && p2 > small_t)
      {
        hit = {this, p2, 0};
      }

      if(p2 < small_t && p1 > small_t)
      {
        hit = {this, p1, 0};
      }

      if(p1<p2)
      {
        hit = {this,p1,0};
      }
      else
      {
        hit = {this, p2, 0};
      }
    }

    return hit;
}


vec3 Sphere::Normal(const vec3& point, int part) const
{
    //vec3 normal;
    //TODO; // compute the normal direction
    return (point - this->center).normalized();
}

Box Sphere::Bounding_Box(int part) const
{
    Box box;
    TODO; // calculate bounding box
    return box;
}
